<?xml version="1.0" encoding="UTF-8"?>
<!--
  Transform: InventoryRequest → InventoryResponse
  Actor:     Inventory
  Purpose:   Confirm stock availability and assign warehouse locations to each item.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="newId"/>
  <xsl:param name="currentDate"/>

  <xsl:template match="/">
    <InventoryResponse>
      <ResponseId><xsl:value-of select="$newId"/></ResponseId>
      <RequestReference><xsl:value-of select="InventoryRequest/RequestId"/></RequestReference>
      <OrderReference><xsl:value-of select="InventoryRequest/OrderReference"/></OrderReference>
      <SupplierId><xsl:value-of select="InventoryRequest/SupplierId"/></SupplierId>
      <BuyerId><xsl:value-of select="InventoryRequest/BuyerId"/></BuyerId>
      <ResponseDate><xsl:value-of select="$currentDate"/></ResponseDate>
      <OverallStatus>AVAILABLE</OverallStatus>
      <PaymentTerms><xsl:value-of select="InventoryRequest/PaymentTerms"/></PaymentTerms>
      <Currency><xsl:value-of select="InventoryRequest/Currency"/></Currency>

      <xsl:copy-of select="InventoryRequest/DeliveryAddress"/>

      <ConfirmedItems>
        <xsl:for-each select="InventoryRequest/RequestItems/RequestItem">
          <ConfirmedItem>
            <LineNumber><xsl:value-of select="LineNumber"/></LineNumber>
            <ProductCode><xsl:value-of select="ProductCode"/></ProductCode>
            <Description><xsl:value-of select="Description"/></Description>
            <ConfirmedQuantity><xsl:value-of select="RequiredQuantity"/></ConfirmedQuantity>
            <UnitPrice><xsl:value-of select="UnitPrice"/></UnitPrice>
            <LineTotal><xsl:value-of select="LineTotal"/></LineTotal>
            <WarehouseId>WH-EAST-01</WarehouseId>
            <WarehouseLocation>East Distribution Centre, Bay <xsl:value-of select="position() + 10"/></WarehouseLocation>
          </ConfirmedItem>
        </xsl:for-each>
      </ConfirmedItems>
    </InventoryResponse>
  </xsl:template>

</xsl:stylesheet>
