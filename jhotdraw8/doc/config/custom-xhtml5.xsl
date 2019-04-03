<?xml version='1.0'?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="xhtml5/docbook.xsl"/>
  <xsl:import href="xhtml/highlight.xsl"/>
  <xsl:param name="highlight.source" select="'1'"/>
  <xsl:param name="callout.graphics" select="0"/>
  <xsl:param name="callout.unicode" select="1"/>
  <!--
 <xsl:param name="use.extensions" select="1"/>
 <xsl:param name="linenumbering.extension" select="1"/>
 -->
  <xsl:param name="generate.toc">
    appendix toc,title
    article/appendix nop
    article toc,title
    book toc,title,figure,table,example,equation
    chapter nop
    part toc,title
    preface toc,title
    qandadiv toc
    qandaset toc
    reference toc,title
    sect1 nop
    sect2 nop
    sect3 nop
    sect4 nop
    sect5 nop
    section nop
    set toc,title
  </xsl:param>
  <xsl:param name="formal.title.placement">
    figure after
    example after
    equation after
    table after
    procedure after
  </xsl:param>
</xsl:stylesheet>
