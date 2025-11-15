API Documentation:

Get Schools:
```
  https://api.haulradar.com/v1/blog/schools/
```
  Example Output:
  
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

Get Events by School:
```
  https://api.haulradar.com/v1/blog/events/school/<School ID>
```
  Example Output:

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

Get Event:
```
  https://api.haulradar.com/v1/blog/events/<Event ID>
```
  Example Output:

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




