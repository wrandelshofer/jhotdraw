<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ @(#)collectbiblio.xsl
  ~ Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                       xmlns="http://docbook.org/ns/docbook"
                       xmlns:db="http://docbook.org/ns/docbook">
  <xsl:output method="xml" indent="yes"/>

 <!-- collect all biblio references in a key -->
<xsl:key name="bib-refs" match="db:xref[starts-with(@linkend, 'bib.')]" use="@linkend"/>

 <!-- print the document skeleton -->
<xsl:template match="/">
<section>
<title>References</title>
  <para></para>
<bibliography>
<xsl:apply-templates/>
</bibliography>
</section>
</xsl:template>

<!-- suppress text and attributes -->
<xsl:template match="text()|@*"/>

 <!-- for each unique biblio reference that we find, print a bibliomixed entry -->
<xsl:template match="db:xref[starts-with(@linkend, 'bib.')]">
<xsl:if test="generate-id() = generate-id(key('bib-refs', @linkend))">
  <bibliomixed>
    <xsl:attribute name="xml:id"><xsl:value-of select="@linkend"/></xsl:attribute>
  </bibliomixed>
</xsl:if>
</xsl:template>

</xsl:stylesheet>
