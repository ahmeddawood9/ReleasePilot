package com.dawood.releasepilot.deployment;

//record = simple immutable data carrier
//this represents input data coming from our backend.


public record CreateDeploymentRequest (String serviceName,String version) {}
