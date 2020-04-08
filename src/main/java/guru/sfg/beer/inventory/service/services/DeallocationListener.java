package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfiguration;
import guru.sfg.brewery.model.event.DeallocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeallocationListener {
    private final AllocationService allocationService;

    @JmsListener(destination = JmsConfiguration.DEALLOCATE_ORDER_QUEUE)
    public void listen(DeallocateOrderRequest deallocateOrderRequest) {
        allocationService.deallocateOrder(deallocateOrderRequest.getBeerOrderDto());
    }
}
