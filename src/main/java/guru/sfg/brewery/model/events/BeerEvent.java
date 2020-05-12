package guru.sfg.brewery.model.events;

import guru.sfg.brewery.model.BeerDto;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Builder
public class BeerEvent implements Serializable {

    static final long serialVersionUID = 6254807710879535981L;

    private final BeerDto beerDto;

}
