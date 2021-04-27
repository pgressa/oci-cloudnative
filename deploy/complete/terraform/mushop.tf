# Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upl.
# 

# Create namespace mushop for the mushop microservices
resource "kubernetes_namespace" "mushop_namespace" {
  metadata {
    name = "mushop"
  }
  depends_on = [oci_containerengine_node_pool.oke_node_pool]
}

# Deploy mushop chart
resource "helm_release" "mushop" {
  name      = "mushop"
  chart     = "../helm-chart/mushop"
  namespace = kubernetes_namespace.mushop_namespace.id
  wait      = false

  set {
    name  = "global.mock.service"
    value = var.mushop_mock_mode_all ? "all" : "none"
  }
  set {
    name  = "global.oadbAdminSecret"
    value = var.db_admin_name
  }
  set {
    name  = "global.oadbConnectionSecret"
    value = var.db_connection_name
  }
  set {
    name  = "global.oadbWalletSecret"
    value = var.db_wallet_name
  }
  set {
    name  = "global.ossConnectionSecret"
    value = var.oss_conection
  }
  # set {
  #   name  = "global.oosBucketSecret" # Commented until come with solution to gracefull removal of objects when terraform destroy
  #   value = var.oos_bucket_name
  # }
  set {
    name  = "tags.atp"
    value = var.mushop_mock_mode_all ? false : true
  }
  set {
    name  = "tags.streaming"
    value = var.mushop_mock_mode_all ? false : true
  }

  depends_on = [helm_release.ingress_nginx] # Ugly workaround because of the oci pvc provisioner not be able to wait for the node be active and retry.

  timeout = 500
}
