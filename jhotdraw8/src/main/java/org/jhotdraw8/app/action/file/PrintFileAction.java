/*
 * @(#)PrintFileAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.file;

import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.DocumentView;
import org.jhotdraw8.app.action.AbstractViewAction;
import org.jhotdraw8.util.Resources;

/**
 * Presents a printer chooser to the user and then prints the
 * {@link org.jhotdraw8.app.ProjectView}.
 * <p>
 * This action requires that the view implements the {@code PrintableView}
 * interface.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PrintFileAction extends AbstractViewAction<DocumentView> {

    private static final long serialVersionUID = 1L;

    public static final String ID = "file.print";

    /**
     * Creates a new instance.
     *
     * @param app the application
     * @param view the view
     */
    public PrintFileAction(Application<DocumentView> app, DocumentView view) {
        super(app, view);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    /*
    @Override
    public void actionPerformed(ActionEvent evt) {
        PrintableView view = (PrintableView)getActiveView();
        view.setEnabled(false);
        if ("true".equals(System.getProperty("apple.awt.graphics.UseQuartz", "false"))) {
            printQuartz(view);
        } else {
            printJava2D(view);
        }
        view.setEnabled(true);
    }
    /*
     * This prints at 72 DPI only. We might need this for some JVM versions on
     * Mac OS X.* /

    public void printJava2D(PrintableView v) {
        Pageable pageable = v.createPageable();
        if (pageable == null) {
            throw new InternalError("ProjectView does not have a method named java.awt.Pageable createPageable()");
        }

        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            // FIXME - PrintRequestAttributeSet should be retrieved from ProjectView
            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new PrinterResolution(300, 300, PrinterResolution.DPI));
            job.setPageable(pageable);
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException e) {
                    String message = (e.getMessage() == null) ? e.toString() : e.getMessage();
                    ProjectView view = getActiveView();
                    Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
                    JSheet.showMessageSheet(view.getComponent(),
                            "<html>" + UIManager.getString("OptionPane.css") +
                            "<b>" + labels.getString("couldntPrint") + "</b><br>" +
                            ((message == null) ? "" : message));
                }
            } else {
                System.out.println("JOB ABORTED!");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    /*
     * This prints at 72 DPI only. We might need this for some JVM versions on
     * Mac OS X.* /

    public void printJava2DAlternative(PrintableView v) {
        Pageable pageable = v.createPageable();
        if (pageable == null) {
            throw new InternalError("ProjectView does not have a method named java.awt.Pageable createPageable()");
        }

        try {
            final PrinterJob job = PrinterJob.getPrinterJob();
            PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
            attr.add(new PrinterResolution(300, 300, PrinterResolution.DPI));
            job.setPageable(pageable);
            if (job.printDialog(attr)) {
                try {
                    job.print();
                } catch (PrinterException e) {
                    Resources labels = Resources.getResources("org.jhotdraw8.app.Labels");
                    JSheet.showMessageSheet(getActiveView().getComponent(),
                            labels.getFormatted("couldntPrint", e));
                }
            } else {
                System.out.println("JOB ABORTED!");
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * On Mac OS X with the Quartz rendering engine, the following code achieves
     * the best results.
     * /
    public void printQuartz(PrintableView v) {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(v.getComponent());
        final Pageable pageable = v.createPageable();
        final double resolution = 300d;
        JobAttributes jobAttr = new JobAttributes();
        // FIXME - PageAttributes should be retrieved from ProjectView
        PageAttributes pageAttr = new PageAttributes();
        pageAttr.setMedia(PageAttributes.MediaType.A4);
        pageAttr.setPrinterResolution((int) resolution);
        final PrintJob pj = frame.getToolkit().getPrintJob(
                frame,
                "Job Title",
                jobAttr,
                pageAttr);

        getActiveView().setEnabled(false);
        new BackgroundTask() {

            @Override
            protected void construct() throws PrinterException {

                // Compute page format from settings of the print job
                Paper paper = new Paper();
                paper.setSize(
                        pj.getPageDimension().width / resolution * 72d,
                        pj.getPageDimension().height / resolution * 72d);
                paper.setImageableArea(64d, 32d, paper.getWidth() - 96d, paper.getHeight() - 64);
                PageFormat pageFormat = new PageFormat();
                pageFormat.setPaper(paper);

                // Print the job
                try {
                    for (int i = 0,  n = pageable.getNumberOfPages(); i < n; i++) {
                        PageFormat pf = pageable.getPageFormat(i);
                        pf = pageFormat;
                        Graphics g = pj.getGraphics();
                        if (g instanceof Graphics2D) {
                            pageable.getPrintable(i).print(g, pf, i);
                        } else {
                            BufferedImage buf = new BufferedImage(
                                    (int) (pf.getImageableWidth() * resolution / 72d),
                                    (int) (pf.getImageableHeight() * resolution / 72d),
                                    BufferedImage.TYPE_INT_RGB);
                            Graphics2D bufG = buf.createGraphics();


                            bufG.setBackground(Color.WHITE);
                            bufG.fillRect(0, 0, buf.getWidth(), buf.getHeight());
                            bufG.scale(resolution / 72d, resolution / 72d);
                            bufG.translate(-pf.getImageableX(), -pf.getImageableY());
                            pageable.getPrintable(i).print(bufG, pf, i);
                            bufG.dispose();
                            g.drawImage(buf,
                                    (int) (pf.getImageableX() * resolution / 72d),
                                    (int) (pf.getImageableY() * resolution / 72d),
                                    null);
                            buf.flush();
                        }
                        g.dispose();
                    }
                } finally {
                    pj.end();
                }
            }

            @Override
            protected void failed(Throwable error) {
               error.printStackTrace();
            }
            @Override
            protected void finished() {
                getActiveView().setEnabled(true);
            }
        }.start();
    }
    /**
     * Returns true if the action is enabled.
     * The enabled state of the action depends on the state that has been set
     * using setEnabled() and on the enabled state of the application.
     *
     * @return true if the action is enabled, false otherwise
     * @see Action#isEnabled
     * /
    @Override public boolean isEnabled() {
        return super.isEnabled() && (getActiveView() instanceof PrintableView);
    }*/

    @Override
    protected void onActionPerformed(javafx.event.ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
