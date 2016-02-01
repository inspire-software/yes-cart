<div ng-app="shopApp" class="ng-cloak">
    <div class="generic-container" ng-controller="ShopController as ctrl">
        <div class="panel panel-default">
            <!-- Default panel contents -->
            <div class="panel-heading"><span class="lead">List of Shops </span></div>
            <div class="tablecontainer">
                <table class="table table-hover">
                    <thead>
                    <tr>
                        <th>ID.</th>
                        <th>Name</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="u in ctrl.shops">
                        <td><span ng-bind="u.id"></span></td>
                        <td><span ng-bind="u.name"></span></td>
                        <!--
                                            <td><span ng-bind="u.address"></span></td>
                                            <td><span ng-bind="u.email"></span></td>
                                            <td>
                                                <button type="button" ng-click="ctrl.edit(u.id)" class="btn btn-success custom-width">Edit</button>
                                                <button type="button" ng-click="ctrl.remove(u.id)" class="btn btn-danger custom-width">Remove</button>
                                            </td>
                        -->
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

