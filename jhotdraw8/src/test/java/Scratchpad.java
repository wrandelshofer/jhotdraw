/* @(#)Scratchpad.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
import java.text.ParseException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


/**
 *
 * @author werni
 */
public class Scratchpad {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ParseException {
        StringBuilder textArea = new StringBuilder("hello ");
final CompletableFuture<Void> promise = 
        CompletableFuture.supplyAsync(()->{
            try {
                Thread.sleep(1000);
               if (true) throw new InternalError("blunder");
            } catch (InterruptedException ex) {
                throw new CompletionException(ex);
            }
                return "world";
        }).thenAccept(value->{
                    textArea.append(value);
        } );
        
        promise.thenRun(()->System.out.println("done "+textArea))    
                .thenRun(()->System.out.println("done2 "+textArea))  
                .handle((v,ex)->{
                if (ex!=null)System.err.println("kata");
                return null;
                })
        .exceptionally(ex->{System.out.println("ex1:"+ex);return null;})
        .exceptionally(ex->{System.out.println("ex2:"+ex);return null;})
                ;
        System.out.println("first or last?");
    }

}
