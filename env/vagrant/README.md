# Development environment

Vagrant project to create development env, which allow to build and run solution on local workstation. 

vagrant plugin install vagrant-disksize

## Well known issues

 * In case if provisioning failed due login failure into vagrant -  try to uncomment lines 29,30 in Vagrant file
 * Sometimes vagrant provisionong is failed, because url to download mvn is changed - fix url
 * In case of ENOENT error, like provided below

```
> yes-cart-jam@3.4.0 build.dev /home/vagrant/yes-cart/manager/jam-jsclient/src/main/typescript
> gulp build.dev --color --env-config dev

sh: 1: gulp: not found
npm ERR! file sh
npm ERR! code ELIFECYCLE
npm ERR! errno ENOENT
npm ERR! syscall spawn
npm ERR! yes-cart-jam@3.4.0 build.dev: `gulp build.dev --color --env-config dev`
npm ERR! spawn ENOENT
npm ERR!
npm ERR! Failed at the yes-cart-jam@3.4.0 build.dev script.
npm ERR! This is probably not a problem with npm. There is likely additional logging output above.
npm WARN Local package.json exists, but node_modules missing, did you mean to install?
```

Force run npm install

```
npm install in /yes-cart/manager/jam-jsclient/src/main/typescript
cd ~/yes-cart
mvn install -Pmysql -PpaymentAll -Pdev -DskipTests=true
```


## Build steps

 **IMPORTANT** do not omit clean flag at first build

```
git clone https://github.com/inspire-software/yes-cart.git --depth 30
cd yes-cart
mvn clean install -Pmysql -PpaymentAll -Pdev -DskipTests=true
```
