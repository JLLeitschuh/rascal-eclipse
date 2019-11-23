package org.rascalmpl.eclipse.views.xterm;

import static org.rascalmpl.eclipse.IRascalResources.ID_RASCAL_XTERM_PART;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;
import org.rascalmpl.eclipse.Activator;
import org.rascalmpl.uri.URIUtil;

public class XtermView extends ViewPart {
    public static final String ID = ID_RASCAL_XTERM_PART;

    private Browser browser;
    private volatile String mainLocation;
    private XtermServer server;
    private Object lock = new Object();

    private ExecutorService backgroundTasks;

    public XtermView() { 
        backgroundTasks = Executors.newSingleThreadExecutor(); 
    }


    public void gotoPage(final String page) {
        if (mainLocation == null) {
            // lets wait in the background for the tutor being loaded, we know it will at some point..
            backgroundTasks.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                        gotoPage(page);
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
        else {
            new WorkbenchJob("Loading xterm page") {
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    if (mainLocation == null) {
                        // this shouldn't happen but lets just be sure
                        gotoPage(page);
                    }
                    else {
                        browser.setUrl(mainLocation + page);
                    }
                    return Status.OK_STATUS;
                }
            }.schedule();
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        browser = new Browser(parent, SWT.NONE);
        browser.setText("<html><body>The terminal is now loading: <progress max=\"100\"></progress></body></html>");
        new StarterJob().schedule();
    }

    @Override
    public void setFocus() {
        browser.setFocus();
    }

    @Override
    public void dispose() {
        stop();
    }

    private void stop() {
        if (server != null) {
            try {
                server = null;
            } catch (Exception e) {
                Activator.log("could not stop tutor", e);
            }
        }
    }

    private class StarterJob extends Job {

        public StarterJob() {
            super("Starting xterm");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            synchronized (lock) {
                try {
                    if (server != null) {
                        server.stop(); 
                        server = null;
                    }

                    if (server == null) {
                        server = new XtermServer(9999, URIUtil.correctLocation("http", "localhost:9999", ""));
                    }

                    monitor.worked(1);

                    new WorkbenchJob("Loading xterm start page") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            mainLocation = "http://localhost:" + server.getPort();
                            browser.setUrl(mainLocation + "index.html");
                            return Status.OK_STATUS;
                        }
                    }.schedule();

                }
                catch (Throwable e) {
                    Activator.getInstance().logException("Could not start tutor server", e);
                }
            }

            return Status.OK_STATUS;
        }
    }
}