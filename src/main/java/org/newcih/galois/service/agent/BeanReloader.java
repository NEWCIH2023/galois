package org.newcih.galois.service.agent;

public interface BeanReloader<T> {

    void updateBean(T object);

    boolean validBean(T object);
}
