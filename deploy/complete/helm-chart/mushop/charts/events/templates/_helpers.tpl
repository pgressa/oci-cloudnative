{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "events.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "events.fullname" -}}
{{- if .Values.fullnameOverride -}}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- $name := default .Chart.Name .Values.nameOverride -}}
{{- if contains $name .Release.Name -}}
{{- .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "events.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "events.labels" -}}
app.kubernetes.io/name: {{ include "events.name" . }}
helm.sh/chart: {{ include "events.chart" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/* OSS Configurations */}}
{{- define "events.env.stream" -}}
# Stream connection
- name: ORACLECLOUD_KAFKA_SASL_JAAS_CONFIG
  value: "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"cloudnative-devrel/events-stream-mushop-user-reVW/ocid1.streampool.oc1.iad.amaaaaaabnqp5kqagob4nekk754z57as7w2ligxg37lpj3ghrjayuhxaxznq\" password=\"c[HaEDuKgrF+FKXap8OB\";"
- name: ORACLECLOUD_KAFKA_BOOTSTRAP_SERVERS
  value: "cell-1.streaming.us-ashburn-1.oci.oraclecloud.com:9092"
{{- end -}}

{{/* OAPM Connection url */}}
{{- define "events.oapm.connection" -}}
{{- $oapmConnection := .Values.oapmConnectionSecret | default (.Values.global.oapmConnectionSecret | default (printf "%s-oapm-connection" .Chart.Name)) -}}
- name: ORACLECLOUD_TRACING_ZIPKIN_HTTP_URL
  valueFrom:
    secretKeyRef:
      name: {{ $oapmConnection }}
      key: zipkin_url
- name: ORACLECLOUD_TRACING_ZIPKIN_HTTP_PATH
  valueFrom:
    secretKeyRef:
      name: {{ $oapmConnection }}
      key: zipkin_path
{{- end -}}

{{/* OIMS configuration */}}
{{- define "events.oims.config" -}}
{{- $ociDeployment := .Values.ociDeploymentConfigMap | default (.Values.global.ociDeploymentConfigMap | default (printf "%s-oci-deployment" .Chart.Name)) -}}
- name: ORACLECLOUD_METRICS_COMPARTMENT_ID
  valueFrom:
    configMapKeyRef:
      name: {{ $ociDeployment }}
      key: compartment_id
{{- end -}}