REF: YC-749 Perform order splitting by fulfilment centre

Stock management is now organised by fulfilment centre. I.e. each warehouse assigned to shop is now seen as
the fulfilment centre and therefore items in the cart delivered by different fulfilment centres will be
split into several deliveries. Additionally carrier SLA have been improved to have fulfilment centres assignments
allowing business user to configure shipping methods per fulfilment centre.

MAJOR CHANGES
All carrier SLA now must have fulfilment centre/warehouse assignments they are applicable to, without this
carrier SLA is deemed unavailable for fulfilment centre.

CHANGES TO PROD SYSTEMS
1. All active carrier SLA now must have fulfilment centres assignments
2. Note that if you have had more than one warehouse assigned to shop the cart items will be forcefully split into
   separate deliveries.
3. Each item in the order will now have supplier code (fulfilment centre code). Note that for legacy orders this
   values needs to be updated manually in the DB (see examples SQL in schema-changes.sql)
4. Items for which inventory is not tracked (D4 type delivery (Electronic) and D1 with always available items) will
   have an empty supplier code
5. Since each item is now delivered from a specific fulfilment centre all reservations, allocations, cancellations and
   returns will be done against that specific fulfilment centre. Therefore there should be enough stock in a single
   fulfilment centre for the order to be placed

IDEAS AND NEW POSSIBILITIES

Since now every warehouse/ fulfilment centre is treated in isolation it is possible to better organise the inventory
management since all allocations are deterministic (i.e. the stock is allocated and returned to the same warehouse).

Each fulfilment centre can have its own carrier SLA which allows for better management of shipping from specific
fulfilment centres. However it is still possible to allow one SLA to be available for several fulfilment centres as
before.

Because each fulfilment centre has its own delivery in each order it is also easier to integrate with third party
order management systems as each delivery can be integrated with specific fulfilment centre OMS. Additionally the
integration for back order notifications should be also simplified.