<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:h="http://www.w3.org/1999/xhtml"
                xmlns="http://www.w3.org/1999/xhtml">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

  <!-- by default: copy everything and normalize space -->
  <xsl:template match="*/text()[normalize-space()]">
    <xsl:value-of select="normalize-space()"/>
  </xsl:template>
  <xsl:template match="*/text()[not(normalize-space())]"/>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- start of document -->
  <xsl:template match="/">
    <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
    <xsl:apply-templates/>
  </xsl:template>

  <!-- fix links to other pages, because we use now the extension .xhtml -->
  <xsl:template match="@href[substring(., string-length()-4)='.html']">
    <xsl:attribute name="href"><xsl:value-of select="concat(substring(.,1, string-length()-4),'xhtml')"/></xsl:attribute>
  </xsl:template>

  <!-- remove section inside h3 element but keep the children -->
  <xsl:template match="h:h3/h:section">
    <!-- Apply identity transform on child elements of section-->
    <xsl:apply-templates select="*|text()"/>
  </xsl:template>

  <!-- drop empty sections -->
  <xsl:template match="h:section[not(node()|text())]"/>


  <!-- drop unwanted elements -->
  <xsl:template match="h:meta[@name='generator']"/>
  <xsl:template match="h:script"/>

  <!-- drop google fonts -->
  <xsl:template match="h:link[starts-with(@href,'http://fonts.googleapis.com')]"/>
</xsl:stylesheet>