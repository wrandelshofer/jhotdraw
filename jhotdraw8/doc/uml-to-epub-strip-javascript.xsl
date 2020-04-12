<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xi="http://www.w3.org/2001/XInclude"
                xmlns:h="http://www.w3.org/1999/xhtml"
                xmlns="http://www.w3.org/1999/xhtml">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  <xsl:template match="*/text()[normalize-space()]">
    <xsl:value-of select="normalize-space()"/>
  </xsl:template>
  <xsl:template match="*/text()[not(normalize-space())]"/>
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <xsl:template match="/">
    <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text>
    <xsl:apply-templates/>
  </xsl:template>
  <xsl:template match="h:meta[@name='generator']"/>
  <xsl:template match="h:script"/>
  <xsl:template match="xi:include/@href[substring(., string-length()-3)='.html']">
    <xsl:attribute name="href">
      <xsl:value-of select="concat(substring(.,1, string-length()-3),'xhtml')"/>
    </xsl:attribute>
  </xsl:template>
  <!--
  <xsl:template match="h:img">
    <object><xsl:attribute name="type">image/svg+xml</xsl:attribute><xsl:attribute name="data"><xsl:value-of select="@src" /></xsl:attribute><xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute><xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute></object>
  </xsl:template>
  -->
</xsl:stylesheet>