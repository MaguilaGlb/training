# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Purpose

This is a training repository for practicing Git workflows (branching, merging, pull requests, releases). It is not an active development project.

## Repository Contents

### config.json
Contains configuration for a Vert.x-based HTTP server with the following key settings:
- **vertxOptions**: Event loop configuration and HA settings
- **ServerHttpVerticle**: HTTP server configuration (port 8080, `/ping` endpoint)
- **configStoreOptions**: External configuration retrieval via HTTP from localhost:3000

The configuration structure suggests this was used to test configuration management and retrieval patterns in a Vert.x application, though the actual application code is not present in this repository.

## Git Workflow Patterns

The commit history demonstrates standard Git flow practices:
- Feature branches merged to develop
- Release branches (e.g., `release-1.1`, `release/V1.0.0`)
- Tagged releases (e.g., `V1.0.0`)
- Pull request workflow
- Branch merging strategies

When making changes to this repository, follow the established Git flow pattern of creating feature branches and merging through develop before release.
