{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "fulfillment.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "fulfillment.fullname" -}}
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
{{- define "fulfillment.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels
*/}}
{{- define "fulfillment.labels" -}}
app.kubernetes.io/name: {{ include "fulfillment.name" . }}
helm.sh/chart: {{ include "fulfillment.chart" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
run: {{ include "fulfillment.name" . }}
{{- end -}}

{/* OAPM Connection url */}}
{{- define "fulfillment.oapm.connection" -}}
{{- $oapmConnection := .Values.oapmConnectionSecret | default (.Values.global.oapmConnectionSecret | default (printf "%s-oapm-connection" .Release.Name)) -}}
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
- name: TRACING_ZIPKIN_ENABLED
  valueFrom:
    secretKeyRef:
      name: {{ $oapmConnection }}
      key: zipkin_enabled
{{- end -}}

{{/* OIMS configuration */}}
{{- define "fulfillment.oims.config" -}}
{{- $ociDeployment := .Values.ociDeploymentConfigMap | default (.Values.global.ociDeploymentConfigMap | default (printf "%s-oci-deployment" .Chart.Name)) -}}
- name: ORACLECLOUD_METRICS_COMPARTMENT_ID
  valueFrom:
    configMapKeyRef:
      name: {{ $ociDeployment }}
      key: compartment_id
{{- end -}}