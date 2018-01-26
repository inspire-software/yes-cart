REF: YC-852 Upgrade Wicket

Upgrade overview:

- Changed namespace for HTML markup (<html  xmlns:wicket="http://wicket.apache.org">)

- Application resource loaders/finders are now collection.
  affected: StorefrontApplication

- CreateEditAddressPage, ResultPage and AuthorizeNetSimPaymentOkPage now moved to "page" root as there are issues
  with resolving resources if they are placed in sub packages. So ALL pages must be in "org.yes.cart.web.page" package
  affected: AuthorizeNetSimPaymentOkPage, CreateEditAddressPage and ResultPage

- PriceView had to be modified. wicket:enclosure were removed because Wicket could not resolve the chiild elements. Had
  to create wrapper WebMarkupContainer instead with visibility driven by child elements
  affected: PriceView

- Refactoring due to class moving/renaming
  * org.apache.wicket.util.ClassProvider -> org.apache.wicket.util.reference.ClassReference
    affected: WicketPagesMounter, ThemePageProvider, WicketPagesMounterImpl, StorefrontApplication
  * IPageParametersEncoder.decodePageParameters(final Request request) -> IPageParametersEncoder.decodePageParameters(final Url url)
    affected: SeoBookmarkablePageParametersEncoder
  * RadioGroup.onSelectionChanged(Object o) -> RadioGroup.onSelectionChanged(<> o) (now typed parameter)
    affected: ManageAddressesView, RegisterPanel, ShippingView

- Tidy up tasks
  * Removed width/height from FeaturedProducts images, they are not needed, and fixed javascript error for when
    FeaturedProducts is not present on a page
  * AbstractWebPage.determineStatefulComponent() now calls object.getId() instead of object.getMarkupId() to prevent
    WARN/exception triggering
  * Fixed navigation buttons (GridView recalculates the itemsPerPage to be exact rows*clomuns, which caused miss match in
    page links calculations)
  * PriceTierTree, PriceTierNode and RangeNode are now Serializable
  * Default order history view is not "this month" (not "this week"). Seems to be a more convenient choice
  * Ensure that getLocalizer() is not called from constructors
