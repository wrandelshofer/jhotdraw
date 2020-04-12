<xsl:stylesheet version="2.0"
                xmlns:h="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text"/>
  <xsl:strip-space elements="*"/>
  <xsl:template match="text()"/>
  <xsl:template match="h:title"/>
  <xsl:template match="h:script"/>
  <xsl:template match="h:span"/>
  <xsl:template match="h:ol[@id='navigation-tree']//h:a">&lt;itemref idref=&quot;<xsl:value-of select="@href"/>&quot;
    linear=&quot;no&quot;/&gt;
  </xsl:template>
  <xsl:template match="h:ol[@id='navigation-tree']//h:li/h:div[h:span[contains(@class,'Project')]]//h:a">&lt;itemref
    idref=&quot;<xsl:value-of select="@href"/>&quot; linear=&quot;yes&quot;/&gt;
  </xsl:template>
  <xsl:template match="h:ol[@id='navigation-tree']//h:li/h:div[h:span[contains(@class,'Model')]]//h:a">&lt;itemref
    idref=&quot;<xsl:value-of select="@href"/>&quot; linear=&quot;yes&quot;/&gt;
  </xsl:template>
  <xsl:template match="h:ol[@id='navigation-tree']//h:li/h:div[h:span[contains(@class,'Diagram')]]//h:a">&lt;itemref
    idref=&quot;<xsl:value-of select="@href"/>&quot; linear=&quot;yes&quot;/&gt;
  </xsl:template>
</xsl:stylesheet>