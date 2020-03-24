package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfiguration;
import guru.sfg.brewery.model.event.AllocateOrderRequest;
import guru.sfg.brewery.model.event.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocationRequestListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfiguration.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest request) {
        AllocateOrderResult.AllocateOrderResultBuilder builder = AllocateOrderResult.builder();
        builder .beerOrder(request.getBeerOrder());

        try {
            Boolean allocationResult = allocationService.allocateOrder(request.getBeerOrder());
            if(allocationResult) {
                builder.pendingInventory(false);
            }else {
                builder.pendingInventory(true);
            }
        }catch (Exception e) {
            log.error("Allocation failed for order id" + request.getBeerOrder().getId());
            builder.allocationError(true);
        }

        jmsTemplate.convertAndSend(JmsConfiguration.ALLOCATE_ORDER_RESPONSE_QUEUE, builder.build());
    }
}
