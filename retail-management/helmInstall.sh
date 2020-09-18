kubectl create namespace retail

helm install product-test-release -n retail ./product-management/helm/product-management/
helm install customer-test-release -n retail ./customer-management/helm/customer-management/
helm install order-test-release -n retail ./order-management/helm/order-management/