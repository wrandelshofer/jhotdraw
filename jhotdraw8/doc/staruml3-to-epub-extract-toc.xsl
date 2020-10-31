<xsl:stylesheet version="1.0"
                xmlns:h="http://www.w3.org/1999/xhtml"
                xmlns="http://www.daisy.org/z3986/2005/ncx/"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
  <xsl:strip-space elements="*"/>
  <xsl:template match="/">
    <ncx version="2005-1">
      <head>
        <meta name="dtb:uid" content="_d0e2"/>
      </head>
      <docTitle>
        <text>
          <xsl:value-of select="h:html/h:head/h:title"/>
        </text>
      </docTitle>
      <navMap>
        <xsl:apply-templates/>
      </navMap>
    </ncx>
  </xsl:template>

  <xsl:template match="/h:html/h:body/h:div/h:div/h:a">
    <navPoint>
      <navLabel>
        <text>
          <xsl:value-of select="text()"/>
        </text>
      </navLabel>
      <content>
        <xsl:attribute name="src">
          <xsl:value-of select="concat('contents/',@href)"/>
        </xsl:attribute>
      </content>
    </navPoint>
  </xsl:template>

  <xsl:template match="h:ol[@id='navigation-tree']//h:li">
    <navPoint>
      <xsl:apply-templates/>
    </navPoint>
  </xsl:template>

  <xsl:template match="h:ol[@id='navigation-tree']//h:a">
    <navLabel>
      <text>
        <xsl:value-of select="text()"/>
      </text>
    </navLabel>
    <content>
      <xsl:attribute name="src">
        <xsl:value-of select="concat('contents/',@href)"/>
      </xsl:attribute>
    </content>
  </xsl:template>


  <xsl:template match="*/text()"/>
</xsl:stylesheet>