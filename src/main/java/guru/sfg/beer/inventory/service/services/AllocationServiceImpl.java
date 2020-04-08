package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.model.BeerOrderDto;
import guru.sfg.brewery.model.BeerOrderLineDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class AllocationServiceImpl implements AllocationService {

    private final BeerInventoryRepository beerInventoryRepository;

    @Override
    public Boolean allocateOrder(BeerOrderDto beerOrderDto) {
        log.debug("Allocating order id: " + beerOrderDto.getId());
        AtomicInteger totalOrdered = new AtomicInteger();
        AtomicInteger totalAllocated = new AtomicInteger();

        beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
            Integer orderQuantity = beerOrderLineDto.getOrderQuantity()!=null?beerOrderLineDto.getOrderQuantity():0;
            Integer allocatedQuantity = beerOrderLineDto.getQuantityAllocated()!= null?beerOrderLineDto.getQuantityAllocated():0;

            if (orderQuantity - allocatedQuantity > 0) {
                allocateBeerOrderLine(beerOrderLineDto);
            }
            totalOrdered.set(totalOrdered.get() + orderQuantity);
            totalAllocated.set(totalAllocated.get() + allocatedQuantity);
        });
        log.debug("Total ordered: "+totalOrdered.get()+" Total allocated: " + totalAllocated.get());
        return totalAllocated.get() == totalAllocated.get();
    }

    @Override
    public void deallocateOrder(BeerOrderDto beerOrderDto) {
        beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
            BeerInventory beerInventory = BeerInventory.builder()
                    .beerId(beerOrderLineDto.getBeerId())
                    .upc(beerOrderLineDto.getUpc())
                    .quantityOnHand(beerOrderLineDto.getQuantityAllocated())
                    .build();
            beerInventoryRepository.save(beerInventory);
        });
    }

    private void allocateBeerOrderLine(BeerOrderLineDto beerOrderLineDto) {
        List<BeerInventory> beerInventoryList = this.beerInventoryRepository.findAllByUpc(beerOrderLineDto.getUpc());

        beerInventoryList.forEach(beerInventory -> {
            int inventory = beerInventory.getQuantityOnHand()==null?0:beerInventory.getQuantityOnHand();
            int orderQty = beerOrderLineDto.getOrderQuantity()==null?0:beerOrderLineDto.getOrderQuantity();
            int allocatedQty = beerOrderLineDto.getQuantityAllocated()==null?0:beerOrderLineDto.getQuantityAllocated();
            int qtyToAllocate = orderQty - allocatedQty;

            if (inventory>=qtyToAllocate) { //FULL allocation
                inventory = inventory - qtyToAllocate;
                beerOrderLineDto.setQuantityAllocated(qtyToAllocate);
                beerInventory.setQuantityOnHand(inventory);
                this.beerInventoryRepository.save(beerInventory);
            } else if (inventory > 0) { //partial allocation
                beerOrderLineDto.setQuantityAllocated(allocatedQty + inventory);
                beerInventory.setQuantityOnHand(0);
                this.beerInventoryRepository.delete(beerInventory);
            }
        });
    }
}
