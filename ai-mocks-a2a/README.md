# AI Mocks A2A

A mock server for testing Agent-to-Agent (A2A) protocol integrations.

## Overview

This library provides a local [MockAgentServer](./src/commonMain/kotlin/me/kpavlov/aimocks/a2a/MockAgentServer.kt) for
simulating [A2A (Agent-to-Agent) API endpoints](https://google.github.io/A2A/). It simplifies testing by allowing you to
define request expectations and responses without making real network calls.
