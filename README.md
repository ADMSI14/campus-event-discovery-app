# Campus Event Discovery & Management Platform (Android)

## Overview

The Campus Event Discovery & Management Platform is an Android-based mobile application that provides students, faculty, and university communities with a centralized platform for discovering, exploring, and accessing campus events.

The application enables users to browse upcoming events, filter events by institution, view detailed event information, and access event locations through integrated map services. The platform uses RESTful API integration to dynamically retrieve and display event data, providing users with an efficient and responsive mobile experience.

The application follows modern mobile development practices by separating user interface components, business logic, and external service communication to support maintainability, scalability, and future feature expansion.

---

## Project Information

**Course:** CS 2063 – Introduction to Mobile Application Development

**Institution:** University of New Brunswick

**Project Type:** Android Mobile Application

---

# Features

- Browse upcoming campus events by institution
- Dynamic event retrieval through REST API integration
- View detailed event information including:
  - Event title
  - Description
  - Date and time
  - Location details
- Interactive map-based event location visualization
- School/institution selection functionality
- Search and navigation-friendly event discovery workflow
- User preference management
- Error handling for failed network requests
- Responsive Android user interface
- Modular application structure supporting future enhancements

---

# System Design Highlights

- Client-server mobile application architecture
- RESTful API communication for dynamic data retrieval
- Separation of presentation, business logic, and data layers
- Modular Android application design
- Event-driven user interaction model
- Asynchronous network communication
- Structured JSON data processing
- Scalable architecture supporting additional campus services

---

## Architecture at a Glance

| Layer             | Responsibility                                       |
|-------------------|------------------------------------------------------|
| Presentation      | Android Activities, UI layouts, user interaction     |
| Application Logic | Event processing, state management, user preferences |
| Data              | RESTful APIs, Retrofit services, JSON models         |
| External Services | Google Maps API                                      |

---

# Architecture

The application follows a modular Android architecture designed to improve maintainability, scalability, and separation of concerns.

The system separates application responsibilities into distinct layers:

## Presentation Layer

Responsible for user interaction and interface rendering.

Components include:
- Android Activities
- UI layouts
- Event list displays
- Navigation components
- User interaction handling


## Data Layer

Responsible for retrieving and managing external information.

Components include:
- REST API communication
- Retrofit service interfaces
- JSON response processing
- Data model objects


## Application Logic Layer

Responsible for managing application behaviour and data flow.

Components include:
- Event processing logic
- User preference management
- Application state handling
- Error handling mechanisms


## Architectural Principles

- Separation of concerns
- Modular component design
- Reusable software components
- Loose coupling between application layers
- Maintainable object-oriented design
- Scalable mobile application structure

---

## Technologies Used

### Languages
- Kotlin
- Java
- XML

### Frameworks & Libraries
- Android SDK
- Retrofit
- Google Maps SDK

### Build Tools
- Gradle

### Development Tools
- Android Studio
- Git
- GitHub

### Data Formats
- JSON

### APIs
- RESTful Web Services

---

## My Contributions

- Designed and implemented the primary Android user interface for event discovery, navigation, and event visualization.
- Integrated RESTful APIs using Retrofit to dynamically retrieve school and event information.
- Developed UI components for displaying event lists, detailed event information, and user navigation flows.
- Implemented Retrofit service interfaces and JSON model mapping to process dynamic event data from RESTful APIs.
- Integrated Google Maps functionality to support location-based event visualization.
- Applied modular software design principles to separate presentation, networking, and business logic layers.
- Implemented error handling and user feedback mechanisms for improved application reliability.
- Collaborated using Git and GitHub version control workflows.

---

## Key Technical Concepts

- Android application development
- Mobile application architecture
- Client-server architecture
- RESTful API integration
- API communication and data exchange
- JSON parsing and object mapping
- Object-oriented programming principles
- Event-driven programming
- Asynchronous programming
- Modular software architecture
- Separation of concerns
- UI/UX design principles
- Location-based services
- Version control and collaborative software development

---

# Getting Started

## Prerequisites

- Android Studio
- Android SDK
- JDK 17 or later
- Google Maps API key

## Installation

Clone the repository:

```bash
git clone https://github.com/yourusername/campus-event-discovery-platform.git
```

Open the project in Android Studio.

Sync Gradle dependencies.

Configure your Google Maps API key.

Build and run the application on an Android emulator or physical device.

---

## Configuration

This application uses Google Maps services.

Before running the application, replace:
YOUR_GOOGLE_MAPS_API_KEY

in:

app/src/main/res/values/strings.xml

with your own Google Maps API key.

Example:

```xml
<string name="google_maps_key">
    YOUR_GOOGLE_MAPS_API_KEY
</string>
```
---

# API Integration

The application communicates with external RESTful web services to retrieve university and event information dynamically. 
API communication is implemented using **Retrofit**, enabling efficient asynchronous HTTP requests and structured JSON deserialization into application data models.

The following endpoints are consumed by the application.

---

## Retrieve Institutions

Returns the list of supported universities and educational institutions available within the platform.

### Endpoint

```http
GET https://api.haulradar.com/v1/blog/schools/
```

### Response

```json
{
  "data": [
    {
      "id": "9d433dfa-5015-4748-8e77-28dcaa4d03f7",
      "name": "University of New Brunswick"
    },
    {
      "id": "e43943a6-b9b2-4d97-a75f-6d79fa3e951e",
      "name": "Mount Allison University"
    }
  ]
}
```

---

## Retrieve Events by Institution

Returns all upcoming events associated with a selected educational institution.

### Endpoint

```http
GET https://api.haulradar.com/v1/blog/events/school/{schoolId}
```

### Path Parameter

| Parameter  |                 Description                   |
|------------|-----------------------------------------------|
| `schoolId` | Unique identifier of the selected institution |

### Response

```json
{
  "data": [
    {
      "id": "1cf3dcd5-0d20-41a5-87f4-00ac2bce236e",
      "school": "9d433dfa-5015-4748-8e77-28dcaa4d03f7",
      "name": "UNB Residence Orientation",
      "description": "Welcome to UNB",
      "location": "SRID=4326;POINT (-66.46689684501543 45.848150283597036)",
      "created_at": "2025-11-15",
      "event_date_time": "2025-11-15T11:15:18.661510-04:00"
    },
    {
      "id": "716812e0-9831-48ed-894a-8c0d5339e0cd",
      "school": "9d433dfa-5015-4748-8e77-28dcaa4d03f7",
      "name": "Halloween at The Cellar",
      "description": "Halloween party at The Cellar",
      "location": "SRID=4326;POINT (-0.0450959801673889 0.0147347150608942)",
      "created_at": "2025-11-15",
      "event_date_time": "2025-11-15T11:15:18.661510-04:00"
    }
  ]
}
```

---

## Retrieve Event Details

Returns detailed information for a single event.

### Endpoint

```http
GET https://api.haulradar.com/v1/blog/events/{eventId}
```

### Path Parameter

| Parameter | Description |
|-----------|-------------|
| `eventId` | Unique identifier of the selected event |

### Response

```json
{
  "data": {
    "id": "1cf3dcd5-0d20-41a5-87f4-00ac2bce236e",
    "school": "9d433dfa-5015-4748-8e77-28dcaa4d03f7",
    "name": "UNB Residence Orientation",
    "description": "Welcome to UNB",
    "location": "SRID=4326;POINT (-66.46689684501543 45.848150283597036)",
    "created_at": "2025-11-15",
    "event_date_time": "2025-11-15T11:15:18.661510-04:00"
  }
}
```
---

## API Design Highlights

- RESTful client-server communication
- Retrofit service layer abstraction
- JSON serialization and deserialization
- Asynchronous API request handling
- Model-driven data mapping
- Error handling and response validation
- Decoupled networking and presentation layers
- Structured API service architecture
- Scalable integration design supporting future services

---

# Future Enhancements

- University authentication and single sign-on (SSO)
- Personalized event recommendations
- Push notifications for upcoming events
- Calendar synchronization
- Advanced event search and filtering
- Offline event caching
- Analytics dashboard for event engagement
- Integration with university scheduling and room management systems



