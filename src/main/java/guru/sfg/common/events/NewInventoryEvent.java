package guru.sfg.common.events;

import com.fasterxml.jackson.annotation.JsonCreator;

public class NewInventoryEvent extends BeerEvent {

    @JsonCreator
    public NewInventoryEvent(BeerDto beerDto) {
        super(beerDto);
    }
}
