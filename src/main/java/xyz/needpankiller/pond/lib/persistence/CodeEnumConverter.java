
package xyz.needpankiller.pond.lib.persistence;

import jakarta.persistence.AttributeConverter;

public interface CodeEnumConverter<E extends CodeEnum> extends AttributeConverter<E, Integer> {

}