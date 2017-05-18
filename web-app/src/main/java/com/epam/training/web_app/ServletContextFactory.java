package com.epam.training.web_app;


import org.springframework.beans.factory.FactoryBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;


public class ServletContextFactory implements FactoryBean<ServletContext>, ServletContextAware {

    private ServletContext mServletContext;

    @Override
    public ServletContext getObject() throws Exception {
        return mServletContext;
    }

    @Override
    public Class<?> getObjectType() {
        return mServletContext.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.mServletContext = servletContext;
    }
}
