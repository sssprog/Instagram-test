package com.sssprog.instagramtest.mvp;

import junit.framework.TestCase;

import static org.fest.assertions.api.Assertions.assertThat;

public class TestPresenter extends TestCase {

    private Object view;
    private Presenter<Object> presenter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        view = new Object();
        presenter = new Presenter<>();
    }

    public void testViewAction() {
        ViewAction action = new ViewAction();
        presenter.attach(view);
        presenter.runViewAction(action);
        assertThat(action.run).isTrue();

        presenter.detach();
        action = new ViewAction();
        presenter.runViewAction(action);
        assertThat(action.run).isFalse();
        presenter.attach(view);
        assertThat(action.run).isTrue();
    }

    private static class ViewAction implements Runnable {
        boolean run;

        @Override
        public void run() {
            run = true;
        }
    }
}
