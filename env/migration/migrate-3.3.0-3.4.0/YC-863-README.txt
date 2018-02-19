REF: YC-863	Improve CMS editor

This task is aimed and making CMS editor more friendly.

Improvements made:

- changes the elements visualisation to work using "visualblocks" plugin
- added "templates" button to toolbar2 for better access to common components
- removed "print" and "preview" from toolbar, they did not work as expected as preview pop up is too small and
  bootstrap would activate mobile view only
- wicket.configuration is now webapp.configuration

Actions for PROD:

- remove visualisation code from yc-preview.css in custom themes (if you have it, see yc-preview.css in default for reference)

- update "config-cluster.properties" file and change property "wicket.configuration" to "webapp.configuration"

NB: customisations of visual editor

- visualblocks has updated CSS in order to mark borders accoring to device view port
- help has been updated with specific instructions on how to work with CMS editor
- template was customised to add new 'preview' config property. This preview content is what is shown in small preview pop up,
  it is a necessary evil in order to display desktop config in a small window (which is the most reasonable view users
  would expect in a preview).