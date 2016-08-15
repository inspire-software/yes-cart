REF: YC-632 YUM webapp

This is a huge epic for changing YUM (Java+Flex Yc Update Manager) into JAM (Java+Angular Manager).
There are lots of changes, however in terms of migration here are some key points:

env/maven changes:
==================
- Configuration renamed:
  * "cluster.config.yum.node_config" -> "cluster.config.admin.node_config"
  * "webapp.yum.context.path"        -> "webapp.admin.context.path"
  * "webapp.yum.war.name"            -> "webapp.admin.war.name"
- Configuration added/changed:
  * Admin node type is now JAM (as opposed to YUM)
  * Admin node id is now a variable "cluster.config.admin.node_id"

cluster communication:
======================
bradcasting now uses Node.getId() (not Node.getNodeId())