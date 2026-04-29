<?xml version="1.0" encoding="UTF-8"?>
<!--
  Transform: InventoryResponse → ShipmentRequest
  Actor:     Supplier
  Purpose:   Convert confirmed inventory into a pick-and-ship instruction for logistics.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="newId"/>
  <xsl:param name="currentDate"/>
  <xsl:param name="requestedDeliveryDate"/>

  <xsl:template match="/">
    <ShipmentRequest>
      <RequestId><xsl:value-of select="$newId"/></RequestId>
      <InventoryReference><xsl:value-of select="InventoryResponse/ResponseId"/></InventoryReference>
      <OrderReference><xsl:value-of select="InventoryResponse/OrderReference"/></OrderReference>
      <SupplierId><xsl:value-of select="InventoryResponse/SupplierId"/></SupplierId>
      <BuyerId><xsl:value-of select="InventoryResponse/BuyerId"/></BuyerId>
      <RequestDate><xsl:value-of select="$currentDate"/></RequestDate>
      <RequestedDeliveryDate><xsl:value-of select="$requestedDeliveryDate"/></RequestedDeliveryDate>
      <PaymentTerms><xsl:value-of select="InventoryResponse/PaymentTerms"/></PaymentTerms>
      <Currency><xsl:value-of select="InventoryResponse/Currency"/></Currency>

      <xsl:copy-of select="InventoryResponse/DeliveryAddress"/>

      <ShipItems>
        <xsl:for-each select="InventoryResponse/ConfirmedItems/ConfirmedItem">
          <ShipItem>
            <LineNumber><xsl:value-of select="LineNumber"/></LineNumber>
            <ProductCode><xsl:value-of select="ProductCode"/></ProductCode>
            <Description><xsl:value-of select="Description"/></Description>
            <Quantity><xsl:value-of select="ConfirmedQuantity"/></Quantity>
            <UnitPrice><xsl:value-of select="UnitPrice"/></UnitPrice>
            <LineTotal><xsl:value-of select="LineTotal"/></LineTotal>
            <WarehouseId><xsl:value-of select="WarehouseId"/></WarehouseId>
          </ShipItem>
        </xsl:for-each>
      </ShipItems>
    </ShipmentRequest>
  </xsl:template>

</xsl:stylesheet>
