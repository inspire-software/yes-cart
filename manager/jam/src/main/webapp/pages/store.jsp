<%@include file="store-tab.jsp"%>

<form ng-controller="ShopController as ctrl" ng-submit="ctrl.saveOrUpdate(shop)">

  <div class="form-group">
    <label for="code">Code</label>
    <input type="text" class="form-control" id="code" placeholder="Code" ng-model="shop.code">
  </div>

  <div class="form-group">
    <label for="name">Name</label>
    <input type="text" class="form-control" id="name" placeholder="Name" ng-model="shop.name">
  </div>

  <button type="submit" class="btn btn-default">Submit</button>



</form>