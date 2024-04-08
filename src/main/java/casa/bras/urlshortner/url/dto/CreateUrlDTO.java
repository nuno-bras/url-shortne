package casa.bras.urlshortner.url.dto;

import org.hibernate.validator.constraints.URL;

public record CreateUrlDTO(@URL String url) {}
