package org.newcih.service.agent;

import java.io.File;

public interface BeanReloader<T> {

    void updateBean(T object);

    boolean validBean(T object);
}
