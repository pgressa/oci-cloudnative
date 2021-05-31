# MuShop OracleCloud Terraform

The terraform configuration scrips here can be used to completely install MuShop to Oracle Cloud.
This includes both the Oracle Cloud services and MuShop application into Oracle Kubernetes Engine.

## Prerequisites

You need to install `terraform` locally.

## Configuration

Create copy of terraform variables file `terraform.tfvars.example`.

```bash
cp terraform.tfvars.example mushop.tfvars
```

Then edit the `mushop.tfvars`:


1. Configure the OCI authentication and deployment variables:
    ```
    # OCI authentication
    tenancy_ocid     = "ocid1.tenancy....."
    fingerprint      = "" # e.g.: "5f:53:..." or leave blank if using CloudShell
    user_ocid        = "" # e.g.: "ocid1.user..." or leave blank if using CloudShell
    private_key_path = "" # e.g.: "/users/user/.oci/oci_api_key.pem" or leave blank if using CloudShell

    # Deployment compartment
    compartment_ocid = "ocid1.compartment...."

    # region
    region = "us-ashburn-1"
    ```
  
1. Enable streaming service (*optional*):
    ```
    # Streaming
    create_oracle_streaming_service_stream = true
    ```

    Note that by configuring this option the terraform will configure Oracle Cloud Streaming service.
    The default quota allows the account to have 1 stream.
    In case you're out of the quota keep this option disabled, the MuShop application won't be affected.

1. Enable newsletter service (*optional*):
    ```
    # Newsletter
    create_oracle_function_newsletter = true
    newsletter_function_approved_email_address = "micronaut-newsletter@mushop.com"
    ```
    Note that by configuring this option the terraforem will configure Oracle Functions, Oracle API Gateway and Oracle Email Delivery Service.
    The default quota allows the account to have 1 API Gateway configured.
    In case you're out of the quota keep this option disabled, the MuShop application won't be affected.

## Installation
1. Init the providers:
    ```bash
    # From this directory
    terraform init
    
    # From repository root directory
    terraform init -chdir deploy/complete/terraform
    ```

2. Plan the execution:
    ```bash
    # From this directory
    terraform plan --var-file mushop.tfvars
    
    # From repository root directory
    terraform plan --var-file mushop.tfvars -chdir deploy/complete/terraform
    ```

3. Plan the execution:
    ```bash
    # From this directory
    terraform apply --var-file mushop.tfvars
    
    # From repository root directory
    terraform apply --var-file mushop.tfvars -chdir deploy/complete/terraform
    ```
