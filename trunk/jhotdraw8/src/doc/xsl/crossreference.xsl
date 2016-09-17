<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml"/>

  <!-- TODO customize transformation rules 
       syntax recommendation http://www.w3.org/TR/xslt 
  -->
<xsl:template match="/">
<section xmlns="http://docbook.org/ns/docbook" version="5.0" xml:lang="en">
<title>Cross Reference</title>
<para>This section is empty.</para>
<xsl:apply-templates/>
</section>
</xsl:template>

<xsl:template match="section">
  <content comment="{key('el-by-idref', @id)}" referencedid="{@id}" test="{@id}"/>
</xsl:template>

</xsl:stylesheet>
