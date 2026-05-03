<?xml version="1.0" encoding="UTF-8"?>
<!--
  Transform: Invoice → PaymentInstruction
  Actor:     Buyer
  Purpose:   Convert supplier invoice into a bank transfer instruction with account details.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:param name="newId"/>
  <xsl:param name="currentDate"/>

  <xsl:template match="/">
    <PaymentInstruction>
      <InstructionId><xsl:value-of select="$newId"/></InstructionId>
      <InvoiceReference><xsl:value-of select="Invoice/InvoiceId"/></InvoiceReference>
      <OrderReference><xsl:value-of select="Invoice/OrderReference"/></OrderReference>
      <InstructionDate><xsl:value-of select="$currentDate"/></InstructionDate>
      <PaymentDueDate><xsl:value-of select="Invoice/DueDate"/></PaymentDueDate>
      <Currency><xsl:value-of select="Invoice/Currency"/></Currency>
      <PaymentMethod>BANK_TRANSFER</PaymentMethod>
      <PaymentReference>PAY-REF-<xsl:value-of select="Invoice/OrderReference"/></PaymentReference>

      <PayFrom>
        <BuyerId><xsl:value-of select="Invoice/BillTo/BuyerId"/></BuyerId>
        <BankAccount>IBAN-GB-BUYER001-87654321</BankAccount>
        <BankName>Buyer Commerce Bank</BankName>
      </PayFrom>

      <PayTo>
        <SupplierId><xsl:value-of select="Invoice/BillFrom/SupplierId"/></SupplierId>
        <BankAccount><xsl:value-of select="Invoice/BillFrom/BankAccount"/></BankAccount>
        <BankName><xsl:value-of select="Invoice/BillFrom/BankName"/></BankName>
        <BankBIC><xsl:value-of select="Invoice/BillFrom/BankBIC"/></BankBIC>
      </PayTo>

      <Amount><xsl:value-of select="Invoice/TotalAmount"/></Amount>
    </PaymentInstruction>
  </xsl:template>

</xsl:stylesheet>
