kubectl apply -f pega-namespace.yaml
kubectl label namespace pega istio-injection=enabled
kubectl create -f pega-db.yaml
kubectl create -f pega-service.yaml

kubectl create -n istio-system secret tls istio-ingressgateway-certs --key student.example.com/3_application/private/student.example.com.key.pem --cert student.example.com/3_application/certs/student.example.com.cert.pem

# verify that secret is created
# kubectl exec -it -n istio-system $(kubectl -n istio-system get pods -l istio=ingressgateway -o jsonpath='{.items[0].metadata.name}') -- ls -al /etc/istio/ingressgateway-certs


kubectl create -f istios-routing.yaml

export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
export SECURE_INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="https")].nodePort}')
export INGRESS_HOST=$(minikube ip)

# curl -v -HHost:student.example.com --resolve student.example.com:$SECURE_INGRESS_PORT:$INGRESS_HOST --cacert student.example.com/2_intermediate/certs/ca-chain.cert.pem https://student.example.com:$SECURE_INGRESS_PORT/search?name=Pratik

# troubleshoot: kubectl delete pod -n istio-system -l istio=ingressgateway
# for mutual tls
# kubectl create -n istio-system secret generic istio-ingressgateway-ca-certs --from-file=student.example.com/2_intermediate/certs/ca-chain.cert.pem
# kubectl apply -f mutual-tls.yaml
# run app
# curl -HHost:student.example.com --resolve student.example.com:$SECURE_INGRESS_PORT:$INGRESS_HOST --cacert student.example.com/2_intermediate/certs/ca-chain.cert.pem --cert student.example.com/4_client/certs/student.example.com.cert.pem --key student.example.com/4_client/private/student.example.com.key.pem https://student.example.com:$SECURE_INGRESS_PORT/search?name=Pratik




