package com.sssprog.instagramtest.mvp;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestBlockingPresenter extends TestCase {

    private BlockingView view;
    private BlockingPresenter<BlockingView> presenter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        view = mock(BlockingView.class);
        presenter = new BlockingPresenter<>();
    }

    public void testBehaviourWhenExecutionHappensBeforeAndAfterView() {
        presenter.setExecutingRequest(true);
        presenter.attach(view);
        verify(view).showLoadingDialog();
        presenter.detach();
        verify(view).dismissLoadingDialog();
        presenter.setExecutingRequest(false);
    }

    public void testBehaviourWhenExecutionHappensDuringViewPresent() {
        presenter.attach(view);
        verify(view, never()).showLoadingDialog();
        verify(view, never()).dismissLoadingDialog();
        presenter.setExecutingRequest(true);
        verify(view, times(1)).showLoadingDialog();
        presenter.setExecutingRequest(false);
        verify(view, times(1)).dismissLoadingDialog();
    }

}
