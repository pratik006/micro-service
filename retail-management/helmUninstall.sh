helm uninstall -n retail product-test-release
helm uninstall -n retail customer-test-release
helm uninstall -n retail order-test-release
helm uninstall -n retail messaging-test-release

kubectl delete namespace retail