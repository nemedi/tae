<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/order">
    <invoice>
      <xsl:for-each select="items/item">
        <invoiceItem>
          <description><xsl:value-of select="description"/></description>
          <quantity><xsl:value-of select="quantity"/></quantity>
          <price><xsl:value-of select="price"/></price>
        </invoiceItem>
      </xsl:for-each>
      <customer><xsl:value-of select="customer/name"/></customer>
    </invoice>
  </xsl:template>
</xsl:stylesheet>
