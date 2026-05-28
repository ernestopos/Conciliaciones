package com.conciliaciones.domain.common;

import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class BaseDomainEntity<ID extends Serializable> implements Serializable {

    private ID id;
}
