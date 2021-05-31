# MuShop Helm Chart

The Helm charts here can be used to install all components of MuShop to the Kubernetes cluster.
For practical purposes, multiple charts are used to separate installation into the following steps:

1. **Not supported yet**  [`setup`](#setup) Installs _optional_ chart dependencies on the cluster 
1. **Not supported yet**  [`provision`](#provision) Provisions OCI resources integrated with Service Broker _(optional)_
1. [`mushop`](#installation) Deploys the MuShop application runtime

## Setup

The `setup` chart includes several recommended installations on the cluster. These
installations represent common 3rd party services, which integrate with
Oracle Cloud Infrastructure or enable certain features within the application.

1. `cd deploy/complete/helm-chart`

1. Update chart dependencies:

    ```text
    helm dependency update ./setup
    ```

    > This is necessary because chart binaries are not included inside the source code

1. Install `setup` chart:

    ```bash
    helm install mushop-utiils setup --namespace mushop-utilities --create-namespace
    ```

    > **NOTE:** It is possible that certain services may conflict with pre-existing installs. If so, try setting `--set <chart>.enabled=false` for any conflicting charts.

> Example setting with alternate LoadBalancer port:

```text
helm install setup --set ingress-nginx.controller.service.ports.http=8000
```

The installed dependencies are listed below. Note that any can be disabled as needed.

| Chart                                                                                                      | Purpose                                | Option                   |
| ---------------------------------------------------------------------------------------------------------- | -------------------------------------- | ------------------------ |
| [Prometheus](https://github.com/prometheus-community/helm-charts/blob/main/charts/prometheus/README.md)                       | Service metrics aggregation            | `prometheus.enabled`     |
| [Grafana](https://github.com/grafana/helm-charts/blob/main/charts/grafana/README.md)                             | Infra/Service visualization dashboards | `grafana.enabled`        |
| [Metrics Server](https://github.com/helm/charts/blob/master/stable/metrics-server/README.md)               | Support for Horizontal Pod Autoscaling | `metrics-server.enabled` |
| [Service Catalog](https://github.com/kubernetes-sigs/service-catalog/blob/master/charts/catalog/README.md) | Interface for Oracle Service Broker    | `catalog.enabled`        |
| [Ingress Nginx](https://kubernetes.github.io/ingress-nginx/)                 | Load Balancer ingress control          | `ingress-nginx.enabled`  |

## Provision

The `provision` chart is an application of the open-source [OCI Service Broker](https://github.com/oracle/oci-service-broker)
for _provisioning_ Oracle Cloud Infrastructure services. This implementation utilizes [Open Service Broker](https://github.com/openservicebrokerapi/servicebroker/blob/v2.14/spec.md) in Oracle Container Engine for Kubernetes or in other Kubernetes clusters.

Using the `provision` chart is **OPTIONAL**, and will replace the manual ATP provisioning steps outlined below

See [./provision/README.md](./provision/README.md) for complete usage details.

## Installation

### Configuration

Deploying the full application requires cloud backing services from Oracle Cloud Infrastructure.
These services are configured using kubernetes secrets. 


1. Provision an Autonomous Transaction Processing (ATP) database. Once **RUNNING** download the DB Connection Wallet and configure secrets as follows:

    - Create `oadb-admin` secret containing the database administrator password. Used once for schema initializations.

        ```shell
        kubectl create secret generic oadb-admin \
          --namespace mushop \
          --from-literal=oadb_admin_pw='<DB_ADMIN_PASSWORD>'
        ```

    - Create `oadb-wallet` secret with the Wallet _contents_ using the downloaded `Wallet_*.zip`. The extracted `Wallet_*` directory is specified as the secret contents.

        ```shell
        kubectl create secret generic oadb-wallet \
          --namespace mushop \
          --from-file=<PATH_TO_EXTRACTED_WALLET_FOLDER>
        ```

    - Create `oadb-connection` secret with the Wallet **password** and the service **TNS name** to use for connections.

        ```shell
        kubectl create secret generic oadb-connection \
          --namespace mushop \
          --from-literal=oadb_wallet_pw='<DB_WALLET_PASSWORD>' \
          --from-literal=oadb_service='<DB_TNS_NAME>' \
          --from-literal=oadb_ocid='<DB_OCID>' \
        ```

        > Each database has 5 unique TNS Names displayed when the Wallet is downloaded an example would be `mushopdb_TP`.

1. **Optional**: Instead of creating a shared database for the entire application, you may establish full separation of services by provisioning _individual_ ATP instances for each service that requires a database. To do so, repeat the previous steps for each database,and give each secret a unique name, for example: `carts-oadb-admin`, `carts-oadb-connection`, `carts-oadb-wallet`.

    - `carts`
    - `catalogue`
    - `orders`
    - `user`

1. Provision a Streaming instance from the [Oracle Cloud Infrastructure Console](https://console.us-phoenix-1.oraclecloud.com/storage/streaming), and make note of the created Stream `OCID` value.

    - Create `oss-connection` secret containing the Stream connection details.

        ```shell
        kubectl create secret generic oss-connection \
          --namespace mushop \
          --from-literal=bootstrapServers='<OSS STREAM BOOTSTRAP SERVERS>' \
          --from-literal=jaasConfig='<JAAS CONFIG>'
        ```
    Note: The <JAAS CONFIG> format is: 
    ```
    jaasConfig="org.apache.kafka.common.security.plain.PlainLoginModule required username=\"<USER_COMPARTMENT_NAME>/<USER_NAME>/<OSS_POOL_ID>\" password=\"<USER_TOKEN>\";"
    ```
    Make sure the user has permission to write to the given stream.

1. Configure a config map with deployment details:
    
    ```shell
    kubectl create cm oci-deployment \
      --namespace mushop \
      --from-literal=compartment_id='<COMPARTMENT ID>' \
      --from-literal=region='<OCI REGION>'
    ```
    
1. Make a copy of the [`values-dev.yaml`](./mushop/values-dev.yaml) file in this directory. Then complete the missing values (e.g. secrets) like the following:

    ```yaml
    global:
      ossConnectionSecret: oss-connection     # Name of Stream connection secret
      oadbAdminSecret: oadb-admin             # Name of DB Admin secret
      oadbWalletSecret: oadb-wallet           # Name of Wallet secret
      oadbConnectionSecret: oadb-connection   # Name of DB Connection secret
      ociDeploymentConfigMap: oci-deployment  # Name of Deployment details config map
    tags:
      atp: true                               # General flag to use Oracle Autonomous Database
      streaming: true                         # General flag to use Oracle Streaming Service
    ```

    > **NOTE:** If it's desired to connect a separate databases for a given service, you can specify values specific for each service, such as `carts.oadbAdminSecret`, `carts.oadbWalletSecret`... 

#### Configuring Functions

If you want to configure and use the functions/API Gateway functionality, make sure you create and deploy the function and the API Gateway by following the instructions in the `src/functions/newsletter-subscription` folder.

To configure the `api` chart to use the newsletter subscribe function, add the following snippet to the values file you are planning on deploying:

```yaml
api:
  env:
    newsletterSubscribeUrl: https://[API_GATEWAY_URL]
```

Replace the `API_GATEWAY_URL` with an actual URL you got when you deployed your API gateway instance.

### Dev installation

The default chart installation creates an Ingress resource for development (i.e. simple Ingress, without the DNS and need for Prod/Staging secrets).

Before installing the chart, ensure all [configurations](#configuration) are complete.

```bash
helm install -f myvalues.yaml mymushop mushop
```

If you want to troubleshoot the chart, add the `--dry-run` and `--debug` flags and re-run the command again. For example:

```bash
helm install -f myvalues.yaml mymushop mushop --dry-run --debug
```


## Prod/Test Installation

### Installing cert-manager

You only need to run this if you are installing Mushop on a new cluster _and_ you want to use SSL. You need to install the CRDs first, before running Helm for cert-manager:

```text
kubectl apply \
    -f https://raw.githubusercontent.com/jetstack/cert-manager/release-0.10/deploy/manifests/00-crds.yaml
```

Create the `cert-manager` namespace and label it to disable validation:

```text
kubectl create ns cert-manager
kubectl label namespace cert-manager certmanager.k8s.io/disable-validation="true"
```

Add the JetStack Helm repo:

```text
helm repo add jetstack https://charts.jetstack.io
```

Install the `cert-manager` Helm chart:

```bash
kubectl create ns cert-manager
helm install cert-manager jetstack/cert-manager
```

### Installing Mushop

For prod/test installation, you can use the `values-prod.yaml` and call Helm install and pass in the values file:

```bash
helm install -f /mushop/values-prod.yaml mymushop mushop --dry-run --debug
```


## Creating all/individual YAML files

If you don't want to deploy the charts, you can also render the template and get all YAML files by running the `template` command, providing an output directory and the values file to use.

```bash
helm template -f <VALUES_FILE> mymushop mushop --output-dir <SOME_DIR>
```
