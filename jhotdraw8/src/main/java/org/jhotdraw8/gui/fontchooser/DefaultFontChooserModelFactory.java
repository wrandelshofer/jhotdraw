/* @(#)DefaultFontChooserModelFactory.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.text.Font;
import javax.annotation.Nonnull;
import org.jhotdraw8.util.Resources;

/**
 * DefaultFontChooserModelFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultFontChooserModelFactory {

    public FontChooserModel create() {
        final FontChooserModel model = new FontChooserModel();
        model.setFontCollections(generateCollections(loadFonts()));
        return model;
    }

    @Nonnull
    public CompletableFuture<FontChooserModel> createAsync() {
        CompletableFuture<FontChooserModel> future = new CompletableFuture<>();
        Task<FontChooserModel> task = new Task<FontChooserModel>() {
            @Override
            protected FontChooserModel call() throws Exception {
                return create();
            }

            @Override
            protected void failed() {
                future.completeExceptionally(getException());
            }

            @Override
            protected void succeeded() {
                future.complete(getValue());
            }
        };
        ForkJoinPool.commonPool().execute(task);
        return future;
    }

    @Nonnull
    protected List<FontFamily> loadFonts() {
        List<FontFamily> allFamilies = new ArrayList<>();

        List<String> familyNames = Font.getFamilies();
        Collections.sort(familyNames);
        for (String familyName : familyNames) {
            FontFamily fontFamily = new FontFamily();
            fontFamily.setName(familyName);
            allFamilies.add(fontFamily);
            final List<String> fontNames = Font.getFontNames(familyName);
            for (String fontName : fontNames) {
                FontTypeface fontTypeface = new FontTypeface();
                String shortName = fontName.startsWith(familyName) ? fontName.substring(familyName.length()).trim() : fontName;
                if (shortName.isEmpty()) {
                    shortName = "Regular";
                }
                switch (shortName) {
                    case "Regular":
                    case "Plain":
                    case "Roman":
                        fontTypeface.setRegular(true);
                        break;
                }
                fontTypeface.setName(fontName);
                fontTypeface.setShortName(shortName);
                fontFamily.getTypefaces().add(fontTypeface);
            }
            fontFamily.getTypefaces().sort(Comparator.comparing(FontTypeface::getName));
        }

        return allFamilies;
    }

    @Nonnull
    protected ObservableList<FontCollection> generateCollections(@Nonnull List<FontFamily> families) {
        ObservableList<FontCollection> root = FXCollections.observableArrayList();

        final ResourceBundle labels = Resources.getBundle("org.jhotdraw8.gui.Labels");

        // All fonts
        FontCollection allFonts = new FontCollection(labels.getString("FontCollection.allFonts"), true, families);
        root.add(allFonts);

        // Web core fonts
        // https://en.wikipedia.org/wiki/Core_fonts_for_the_Web
        root.add(
                new FontCollection(labels.getString("FontCollection.web"), true, collectFamiliesNamed(families,
                        "Arial",
                        "Arial Black",
                        "Andale Mono",
                        "Courier New",
                        "Comic Sans MS",
                        "Georgia",
                        "Impact",
                        "Times New Roman",
                        "Trebuchet MS",
                        "Verdana",
                        "Webdings")));

        // PDF Standard Fonts
        // https://en.wikipedia.org/wiki/Portable_Document_Format#Standard_Type_1_Fonts_(Standard_14_Fonts)
        root.add(
                new FontCollection(labels.getString("FontCollection.pdf"), true, collectFamiliesNamed(families,
                        "Courier",
                        "Helvetica",
                        "Symbol",
                        "Times",
                        "Zapf Dingbats")));

        // Java System fonts
        root.add(
                new FontCollection(labels.getString("FontCollection.system"), true, collectFamiliesNamed(families,
                        "Dialog",
                        "DialogInput",
                        "Monospaced",
                        "SansSerif",
                        "Serif",
                        "System")));
        // Serif fonts
        root.add(
                new FontCollection(labels.getString("FontCollection.serif"), collectFamiliesNamed(families,
                        // Fonts on Mac OS X 10.5:
                        "Adobe Caslon Pro",
                        "Adobe Garamond Pro",
                        "American Typewriter",
                        "Arno Pro",
                        "Baskerville",
                        "Baskerville Old Face",
                        "Bell MT",
                        "Big Caslon",
                        "Bodoni SvtyTwo ITC TT",
                        "Bodoni SvtyTwo OS ITC TT",
                        "Bodoni SvtyTwo SC ITC TT",
                        "Book Antiqua",
                        "Bookman Old Style",
                        "Calisto MT",
                        "Chaparral Pro",
                        "Century",
                        "Century Schoolbook",
                        "Cochin",
                        "Footlight MT Light",
                        "Garamond",
                        "Garamond Premier Pro",
                        "Georgia",
                        "Goudy Old Style",
                        "Hoefler Text",
                        "Lucida Bright",
                        "Lucida Fax",
                        "Minion Pro",
                        "Palatino",
                        "Times",
                        "Times New Roman",
                        //
                        // Fonts on Mac OS X 10.6:
                        "Didot",
                        //
                        // Fonts on Mac OS X 10.10:
                        "Bodoni 72",
                        "Bodoni 72 Oldstyle",
                        "Bodoni 72 Smallcaps",
                        //
                        // Fonts on Windows XP:
                        "Palatino Linotype",
                        "Bitstream Vera Serif Bold",
                        "Bodoni MT",
                        "Bodoni MT Black",
                        "Bodoni MT Condensed",
                        "Californian FB",
                        "Cambria",
                        "Cambria Math",
                        "Centaur",
                        "High Tower Text",
                        "Perpetua",
                        "Poor Richard",
                        "Rockwell Condensed",
                        "Slimbach-Black",
                        "Slimbach-BlackItalic",
                        "Slimbach-Bold",
                        "Slimbach-BoldItalic",
                        "Slimbach-Book",
                        "Slimbach-BookItalic",
                        "Slimbach-Medium",
                        "Slimbach-MediumItalic",
                        "Sylfaen",
                        // Fonts on Windows Vista
                        "Andalus",
                        "Angsana New",
                        "AngsanaUPC",
                        "Arabic Typesetting",
                        "Cambria",
                        "Cambria Math",
                        "Constantia",
                        "DaunPenh",
                        "David",
                        "DilleniaUPC",
                        "EucrosiaUPC",
                        "Frank Ruehl",
                        "IrisUPC",
                        "Iskoola Pota",
                        "JasmineUPC",
                        "KodchiangUPC",
                        "Narkisim")));
        // Sans Serif
        root.add(
                new FontCollection(labels.getString("FontCollection.sansSerif"), collectFamiliesNamed(families,
                        // Fonts on Mac OS X 10.5:
                        "Abadi MT Condensed Extra Bold",
                        "Abadi MT Condensed Light",
                        "AppleGothic",
                        "Arial",
                        "Arial Black",
                        "Arial Narrow",
                        "Arial Rounded MT Bold",
                        "Arial Unicode MS",
                        "Bell Gothic Std",
                        "Blair MdITC TT",
                        "Century Gothic",
                        "Frutiger",
                        "Futura",
                        "Geneva",
                        "Gill Sans",
                        "Gulim",
                        "Helvetica",
                        "Helvetica Neue",
                        "Lucida Grande",
                        "Lucida Sans",
                        "Microsoft Sans Serif",
                        "Myriad Pro",
                        "News Gothic",
                        "Tahoma",
                        "Trebuchet MS",
                        "Verdana",
                        //
                        // Fonts on Mac OS X 10.6:
                        "Charcoal",
                        "Euphemia UCAS",
                        //
                        // Fonts on Windows XP:
                        "Franklin Gothic Medium",
                        "Lucida Sans Unicode",
                        "Agency FB",
                        "Berlin Sans FB",
                        "Berlin Sans FB Demi Bold",
                        "Bitstream Vera Sans Bold",
                        "Corbel",
                        "Estrangelo Edessa",
                        "Eras Bold ITC",
                        "Eras Demi ITC",
                        "Eras Light ITC",
                        "Eras Medium ITC",
                        "Franklin Gothic Book",
                        "Franklin Gothic Demi",
                        "Franklin Gothic Demi Cond",
                        "Franklin Gothic Heavy",
                        "Franklin Gothic Medium Cond",
                        "Gill Sans MT",
                        "Gill Sans MT Condensed",
                        "Gill Sans MT Ext Condensed Bold",
                        "Maiandra GD",
                        "MS Reference Sans...",
                        "Tw Cen MT",
                        "Tw Cen MT Condensed",
                        "Tw Cen MT Condensed Extra Bold",
                        //
                        // Fonts on Windows Vista:
                        "Aharoni",
                        "Browallia New",
                        "BrowalliaUPC",
                        "Calibri",
                        "Candara",
                        "Corbel",
                        "Cordia New",
                        "CordiaUPC",
                        "DokChampa",
                        "Dotum",
                        "Estrangelo Edessa",
                        "Euphemia",
                        "Freesia UPC",
                        "Gautami",
                        "Gisha",
                        "Kalinga",
                        "Kartika",
                        "Levenim MT",
                        "LilyUPC",
                        "Malgun Gothic",
                        "Meiryo",
                        "Miriam",
                        "Segoe UI")));

        // Scripts 
        root.add(
                new FontCollection(labels.getString("FontCollection.script"), collectFamiliesNamed(families,
                        // Fonts on Mac OS X 10.5:
                        "Apple Chancery",
                        "Bickham Script Pro",
                        "Blackmoor LET",
                        "Bradley Hand ITC TT",
                        "Brush Script MT",
                        "Brush Script Std",
                        "Chalkboard",
                        "Charlemagne Std",
                        "Comic Sans MS",
                        "Curlz MT",
                        "Edwardian Script ITC",
                        "Footlight MT Light",
                        "Giddyup Std",
                        "Handwriting - Dakota",
                        "Harrington",
                        "Herculanum",
                        "Lithos Pro",
                        "Lucida Blackletter",
                        "Lucida Calligraphy",
                        "Lucida Handwriting",
                        "Marker Felt",
                        "Matura MT Script Capitals",
                        "Mistral",
                        "Monotype Corsiva",
                        "Party LET",
                        "Papyrus",
                        "Santa Fe LET",
                        "Savoye LET",
                        "SchoolHouse Cursive B",
                        "SchoolHouse Printed A",
                        "Skia",
                        "Snell Roundhand",
                        "Tekton Pro",
                        "Trajan Pro",
                        "Zapfino",
                        //
                        // Fonts on Mac OS X 10.6:
                        "Casual",
                        "Chalkduster",
                        //
                        // Fonts on Mac OS X 10.10:
                        "Bradley Hand",
                        "Noteworthy",
                        "Trattatello",
                        //
                        // Fonts on Windows XP:
                        "Blackadder ITC",
                        "Bradley Hand ITC",
                        "Chiller",
                        "Freestyle Script",
                        "French Script MT",
                        "Gigi",
                        "Harlow Solid Italic",
                        "Informal Roman",
                        "Juice ITC",
                        "Kristen ITC",
                        "Kunstler Script",
                        "Magneto Bold",
                        "Maiandra GD",
                        "Old English Text",
                        "Palace Script MT",
                        "Parchment",
                        "Pristina",
                        "Rage Italic",
                        "Ravie",
                        "Script MT Bold",
                        "Tempus Sans ITC",
                        "Viner Hand ITC",
                        "Vivaldi Italic",
                        "Vladimir Script",
                        // Fonts on Windows Vista
                        "Segoe Print",
                        "Segoe Script")));

        // Monospaced
        root.add(
                new FontCollection(labels.getString("FontCollection.monospaced"), collectFamiliesNamed(families,
                        // Fonts on Mac OS X 10.5:
                        "Andale Mono",
                        "Courier",
                        "Courier New",
                        "Letter Gothic Std",
                        "Lucida Sans Typewriter",
                        "Monaco",
                        "OCR A Std",
                        "Orator Std",
                        "Prestige Elite Std",
                        //
                        // Fonts on Mac OS X 10.6:
                        "Menlo",
                        //
                        // Fonts on Windows XP:
                        "Lucida Console",
                        "Bitstream Vera S...",
                        "OCR A Extended",
                        "OCR B",
                        //
                        // Fonts on Windows Vista
                        "Consolas",
                        "DotumChe",
                        "Miriam Fixed",
                        "Rod")));

        // Decorative
        root.add(
                new FontCollection(labels.getString("FontCollection.decorative"), collectFamiliesNamed(families,
                        // Fonts on Mac OS X 10.5:
                        "Academy Engraved LET",
                        "Arial Black",
                        "Bank Gothic",
                        "Bauhaus 93",
                        "Bernard MT Condensed",
                        "Birch Std",
                        "Blackoak Std",
                        "BlairMdITC TT",
                        "Bordeaux Roman Bold LET",
                        "Braggadocio",
                        "Britannic Bold",
                        "Capitals",
                        "Colonna MT",
                        "Cooper Black",
                        "Cooper Std",
                        "Copperplate",
                        "Copperplate Gothic Bold",
                        "Copperplate Gothic Light",
                        "Cracked",
                        "Desdemona",
                        "Didot",
                        "Eccentric Std",
                        "Engravers MT",
                        "Eurostile",
                        "Gill Sans Ultra Bold",
                        "Gloucester MT Extra Condensed",
                        "Haettenschweiler",
                        "Hobo Std",
                        "Impact",
                        "Imprint MT Shadow",
                        "Jazz LET",
                        "Kino MT",
                        "Matura MT Script Capitals",
                        "Mesquite Std",
                        "Modern No. 20",
                        "Mona Lisa Solid ITC TT",
                        "MS Gothic",
                        "Nueva Std",
                        "Onyx",
                        "Optima",
                        "Perpetua Titling MT",
                        "Playbill",
                        "Poplar Std",
                        "PortagoITC TT",
                        "Princetown LET",
                        "Rockwell",
                        "Rockwell Extra Bold",
                        "Rosewood Std",
                        "Santa Fe LET",
                        "Stencil",
                        "Stencil Std",
                        "Stone Sans ITC TT",
                        "Stone Sans OS ITC TT",
                        "Stone Sans Sem ITC TT",
                        "Stone Sans Sem OS ITCTT",
                        "Stone Sans Sem OS ITC TT",
                        "Synchro LET",
                        "Wide Latin",
                        //
                        // Fonts on Mac OS X 10.5:
                        "HeadLineA",
                        //
                        // Fonts on Mac OS X 10.10:
                        "Phosphate",
                        //
                        // Fonts on Windows XP:
                        "Algerian",
                        "Bodoni MT Black",
                        "Bodoni MT Poster Compressed",
                        "Broadway",
                        "Castellar",
                        "Elephant",
                        "Felix Titling",
                        "Franklin Gothic Heavy",
                        "Gill Sans MT Ext Condensed Bold",
                        "Gill Sans Ultra Bold Condensed",
                        "Goudy Stout",
                        "Jokerman",
                        "Juice ITC",
                        "Magneto",
                        "Magneto Bold",
                        "Niagara Engraved",
                        "Niagara Solid",
                        "Poor Richard",
                        "Ravie",
                        "Rockwell Condensed",
                        "Showcard Gothic",
                        "Slimbach-Black",
                        "Slimbach-BlackItalic",
                        "Snap ITC" // Fonts on Windows Vista:
                )));
        root.add(
                new FontCollection(labels.getString("FontCollection.symbols"), collectFamiliesNamed(families,
                        // Fonts on Mac OS X 10.5:
                        "Apple Symbols",
                        "Blackoack Std",
                        "Bodoni Ornaments ITC TT",
                        "EuropeanPi",
                        "Monotype Sorts",
                        "MT Extra",
                        "Symbol",
                        "Type Embellishments One LET",
                        "Webdings",
                        "Wingdings",
                        "Wingdings 2",
                        "Wingdings 3",
                        "Zapf Dingbats",
                        //
                        // Fonts on Mac OS X 10.10:
                        "Bodoni Ornaments",
                        //
                        // Fonts on Windows XP:

                        "Bookshelf Symbol" //
                // Fonts on Windows Vista:
                )));

        return root;

    }

    @Nonnull
    public static ArrayList<FontFamily> collectFamiliesNamed(List<FontFamily> allFamilies, String... names) {
        ArrayList<FontFamily> coll = new ArrayList<FontFamily>();
        HashSet<String> nameMap = new HashSet<String>();
        nameMap.addAll(Arrays.asList(names));
        for (FontFamily family : allFamilies) {
            if (nameMap.contains(family.getName())) {
                coll.add(family);
            }
        }
        return coll;
    }

}
