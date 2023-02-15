package org.newcih.service.reloader;

/**
 * 重新加载Bean服务
 */
public interface BeanReloader {

    /**
     * 添加bean到对应框架上下文
     *
     * @param clazz bean的类类型
     * @param bean  bean实例
     */
    void addBean(Class<?> clazz, Object bean);

}
