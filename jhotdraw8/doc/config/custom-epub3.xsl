<?xml version='1.0'?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:import href="epub3/chunk.xsl"/>
  <xsl:import href="xhtml/highlight.xsl"/>
  <xsl:param name="highlight.source" select="'1'"/>
  <xsl:param name="callout.graphics" select="0"/>
  <xsl:param name="callout.unicode" select="1"/>
  <xsl:param name="generate.toc">
    appendix toc,title
    article/appendix nop
    article toc,title
    book toc,title,figure,table,example,equation
    chapter toc,title
    part toc,title
    preface toc,title
    qandadiv toc
    qandaset toc
    reference toc,title
    sect1 toc
    sect2 toc
    sect3 toc
    sect4 toc
    sect5 toc
    section toc
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
