package mainpage;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 주문 정보를 캐싱하고, 가게별/시간대별 혼잡도를 계산하는 클래스입니다.
 */
public class CongestionManager {

    private final OrderFileManager orderFileManager;
    private List<OrderFileManager.OrderInfo> orderCache;

    public CongestionManager(OrderFileManager orderFileManager) {
        this.orderFileManager = orderFileManager;
        this.orderCache = new ArrayList<>();
        refreshCache(); // 생성 시 한 번 캐시를 로드합니다.
    }

    /**
     * 파일에서 최신 주문 정보를 다시 읽어와 내부 캐시를 갱신합니다.
     */
    public void refreshCache() {
        this.orderCache = orderFileManager.getTodaysOrderInfo();
        System.out.println("혼잡도 관리자: 캐시를 갱신했습니다. 현재 주문 " + orderCache.size() + "건");
    }

    /**
     * 특정 가게의 현재 시간 기준 15분 내 픽업 예정인 주문 건수를 반환합니다.
     */
    public long getCongestionForNext10Min(String storeName) {
        LocalTime now = LocalTime.now();
        LocalTime tenMinutesLater = now.plusMinutes(10);

        return orderCache.stream()
                .filter(info -> info.storeName().equals(storeName))
                .filter(info -> {
                    LocalTime pickupTime = info.pickupTime();
                    return !pickupTime.isBefore(now) && pickupTime.isBefore(tenMinutesLater);
                })
                .count();
    }

    /**
     * [v6] 특정 가게의, 현재 시간부터 시작하는 1시간 동안의 15분 단위 시간대별 주문 건수 목록을 반환합니다.
     * 항상 4개의 시간대 슬롯을 반환하며, 주문이 없으면 0으로 표시됩니다.
     */
    public Map<LocalTime, Long> getCongestionByTimeSlots(String storeName) {
        Map<LocalTime, Long> slotMap = new java.util.LinkedHashMap<>();
        LocalTime now = LocalTime.now();

        // 시작 시간대를 현재 시간이 속한 15분 단위 구간으로 내림 (예: 14:17 -> 14:15, 14:47 -> 14:45)
        LocalTime firstSlot = now.withSecond(0).withNano(0)
                                 .withMinute(now.getMinute() / 15 * 15);

        // 4개의 15분 단위 슬롯을 0으로 초기화
        for (int i = 0; i < 4; i++) {
            slotMap.put(firstSlot.plusMinutes(i * 15L), 0L);
        }

        // 현재 시간 이후의 주문만 필터링
        List<OrderFileManager.OrderInfo> futureOrders = orderCache.stream()
                .filter(info -> info.storeName().equals(storeName))
                .filter(info -> info.pickupTime().isAfter(now))
                .collect(Collectors.toList());

        // 각 주문이 어느 슬롯에 속하는지 계산하여 카운트
        for (OrderFileManager.OrderInfo info : futureOrders) {
            LocalTime pickupTime = info.pickupTime();
            for (LocalTime slotStart : slotMap.keySet()) {
                LocalTime slotEnd = slotStart.plusMinutes(15);
                if (!pickupTime.isBefore(slotStart) && pickupTime.isBefore(slotEnd)) {
                    slotMap.put(slotStart, slotMap.get(slotStart) + 1);
                    break; // 하나의 주문은 하나의 슬롯에만 속함
                }
            }
        }
        return slotMap;
    }

    /**
     * [v3] 특정 가게의, 특정 시간부터 시작하는 5분 구간의 주문 건수를 반환합니다.
     * @param storeName 가게 이름
     * @param startTime 기준 시간
     * @return 해당 5분 구간 내의 주문 건수
     */
    public long getCongestionFor5MinSlot(String storeName, LocalTime startTime) {
        LocalTime endTime = startTime.plusMinutes(5);

        return orderCache.stream()
                .filter(info -> info.storeName().equals(storeName))
                .filter(info -> {
                    LocalTime pickupTime = info.pickupTime();
                    // 픽업 시간이 startTime과 같거나 이후이고, endTime보다 이전인지 확인
                    return !pickupTime.isBefore(startTime) && pickupTime.isBefore(endTime);
                })
                .count();
    }
}
