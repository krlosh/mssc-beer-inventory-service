package guru.sfg.brewery.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import guru.sfg.brewery.model.BeerDto;

public class NewInventoryEvent extends BeerEvent {

    @JsonCreator
    public NewInventoryEvent(BeerDto beerDto) {
        super(beerDto);
    }
}
