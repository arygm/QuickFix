import firebase_admin
from firebase_admin import credentials, firestore
from dataclasses import dataclass
from typing import List

from dataclasses import dataclass, field
from datetime import datetime, timedelta, time
from typing import List, Optional, Tuple
from collections import deque

import random

@dataclass
class Account:
    uid: str
    first_name: str
    last_name: str
    email: str
    birth_date: datetime
    is_worker: bool = False
    active_chats: List[str] = field(default_factory=list)

@dataclass
class Review:
    username: str
    review: str
    rating: float

    def to_firestore_map(self) -> dict:
        return {
            "username": self.username,
            "review": self.review,
            "rating": self.rating
        }
@dataclass
class IncludedService:
    name: str

    def to_firestore_map(self) -> dict:
        return {
            "name": self.name
        }

@dataclass
class AddOnService:
    name: str

    def to_firestore_map(self) -> dict:
        return {
            "name": self.name
        }

@dataclass
class Location:
    latitude: float = 0.0
    longitude: float = 0.0
    name: str = ""

    def to_firestore_map(self) -> dict:
        return {
            "latitude": self.latitude,
            "longitude": self.longitude,
            "name": self.name
        }

@dataclass
class WorkerProfile:
    field_of_work: str = "General Work"  # Default generic field of work
    description: str = "No description available"  # Default description
    location: Optional[Location] = field(default_factory=lambda: Location(0.0, 0.0, "Unknown Location"))
    quick_fixes: List[str] = field(default_factory=lambda: ["Fix a leaking faucet", "Assemble a furniture piece"])
    included_services: List[IncludedService] = field(
        default_factory=lambda: [
            IncludedService(name="Basic Consultation"),
            IncludedService(name="Service Inspection"),
        ]
    )
    add_on_services: List[AddOnService] = field(
        default_factory=lambda: [
            AddOnService(name="Express Delivery"),
            AddOnService(name="Premium Materials"),
        ]
    )
    reviews: deque = field(
        default_factory=lambda: deque(
            [
                Review(username="User1", review="Great service!", rating=5.0),
                Review(username="User2", review="Very satisfied", rating=4.5),
            ]
        )
    )
    profile_picture: str = "https://example.com/default-profile-pic.jpg"
    banner_picture: str = "https://example.com/default-banner-pic.jpg"
    price: float = 50.0
    display_name: str = "Anonymous Worker"
    unavailability_list: List[datetime] = field(
        default_factory=lambda: [datetime.now() + timedelta(days=1), datetime.now() + timedelta(days=3)]
    )
    working_hours: Tuple[time, time] = field(default_factory=lambda: (time(9, 0), time(17, 0)))
    uid: str = "default-uid"
    tags: List[str] = field(default_factory=lambda: ["Reliable", "Experienced", "Professional"])
    rating: float = field(init=False)

    def __post_init__(self):
        # Compute the average rating from reviews
        if self.reviews:
            self.rating = sum(review.rating for review in self.reviews) / len(self.reviews)
        else:
            self.rating = 0.0

def initialize_firestore():
    # Initialize the Firebase app
    cred = credentials.Certificate('../data_loader_service_account_key.json')
    firebase_admin.initialize_app(cred)
    return firestore.client()

def upload_data(db, workers: List[WorkerProfile]):
    for worker in workers:
        # Create or update the worker document
        worker_ref = db.collection('workers').document(worker.uid)
        
        worker_data = {
            "uid": worker.uid,
            "description": worker.description,
            "fieldOfWork": worker.field_of_work,
            "location": worker.location.to_firestore_map() if worker.location else None,
            "price": worker.price,
            "display_name": worker.display_name,
            "included_services": [service.to_firestore_map() for service in worker.included_services],
            "addOnServices": [service.to_firestore_map() for service in worker.add_on_services],
            "workingHours": {
                "start": worker.working_hours[0].isoformat(),
                "end": worker.working_hours[1].isoformat(),
            },
            "unavailability_list": [date.isoformat() for date in worker.unavailability_list],
            "reviews": [review.to_firestore_map() for review in worker.reviews],
            "tags": worker.tags,
            "profileImageUrl": worker.profile_picture,
            "bannerImageUrl": worker.banner_picture,
            "quickFixes": worker.quick_fixes,
        }
        
        worker_ref.set(worker_data)

        # Create the corresponding account document
        account_ref = db.collection('accounts').document(worker.uid)
        account_data = {
            "uid": worker.uid,
            "firstName": f"WorkerFirstName_{worker.uid}",
            "lastName": f"WorkerLastName_{worker.uid}",
            "email": f"worker_{worker.uid}@example.com",
            "birthDate": datetime.now() - timedelta(days=random.randint(7000, 15000)),  # Random birthdate
            "worker": True,
            "activeChats": [],  # Default empty list
        }

        # Upload the account data
        account_ref.set(account_data)

    print("Data uploaded successfully.")

# Feedback categories
positive_feedback = [
    "Amazing experience! Highly recommend.",
    "Fast and reliable service. Will use again.",
    "Very professional and friendly!",
    "Exceeded my expectations. Great work!",
    "Attention to detail was top-notch.",
    "Outstanding job. Worth every penny!",
    "Solved my issue quickly and effectively.",
    "The best service I've experienced so far.",
    "Very skilled and knowledgeable.",
    "Quality work and excellent communication."
]

neutral_feedback = [
    "Service was okay but could be better.",
    "Not bad, but a bit pricey for the work done.",
    "Got the job done, but it took longer than expected.",
    "The service was decent but lacked a personal touch.",
    "Work quality was fine but room for improvement.",
    "Met my expectations but didn’t exceed them.",
    "Good service, but a bit slow.",
    "It was fine, but I’ve had better experiences.",
    "Average experience overall.",
    "Everything was okay, but nothing special."
]

constructive_feedback = [
    "Service was delayed, but the work was good.",
    "Communication could have been better.",
    "A bit unorganized, but they got the job done.",
    "Good work, but not very punctual.",
    "Pricing could be more transparent.",
    "Satisfied with the work, but it could have been cleaner.",
    "Improvement needed in time management.",
    "The quality was okay, but not as promised.",
    "Expected a bit more professionalism.",
    "The work was fine, but customer service could improve."
]

def random_feedback():
    feedback_category = random.choice([positive_feedback, neutral_feedback, constructive_feedback])
    return random.choice(feedback_category)

def random_picture():
    pictures = [
        "https://firebasestorage.googleapis.com/v0/b/quickfix-1fd34.firebasestorage.app/o/profiles%2FHcs3rxQ1rnWSrXIfDIHA3FZdTX12%2Fworker%2Fimage_1733732474039.jpg?alt=media&token=aa8d3850-d31b-4245-a21b-4e7c23df9f5d",
        "https://firebasestorage.googleapis.com/v0/b/quickfix-1fd34.firebasestorage.app/o/profiles%2FHcs3rxQ1rnWSrXIfDIHA3FZdTX12%2Fworker%2Fimage_1733732123975.jpg?alt=media&token=6da6863f-b25a-452d-98a8-519ec8f992d3",
        "https://firebasestorage.googleapis.com/v0/b/quickfix-1fd34.firebasestorage.app/o/profiles%2FHcs3rxQ1rnWSrXIfDIHA3FZdTX12%2Fworker%2Fimage_1733583808196.jpg?alt=media&token=65a00f84-ffe9-4b23-b94a-46d6854a8a90"
    ]
    return random.choice(pictures)


def generate_random_worker_profile(uid, categories):
    category = random.choice(categories)
    subcategory = random.choice(category.subcategories)
    
    # Randomize location within La Suisse Romande
    location = Location(
        latitude=random.uniform(46.0, 47.0),
        longitude=random.uniform(6.0, 7.5),
        name=f"{random.choice(['Geneva', 'Lausanne', 'Fribourg', 'Neuchâtel', 'Sion', 'Yverdon-les-Bains'])} {random.randint(1, 100)}"
    )
    
    # Randomly select included services with a minimum of 5
    included_service_count = random.randint(5, len(subcategory.setService) - 2)  # Ensure room for add-ons
    included_services = [
        IncludedService(name=service) for service in random.sample(subcategory.setService, included_service_count)
    ]
    
    # Determine the remaining services for add-on services
    remaining_services = list(set(subcategory.setService) - set(service.name for service in included_services))
    
    # If remaining services are insufficient, generate dummy services
    if len(remaining_services) < 4:
        remaining_services += [f"Generated Service {i}" for i in range(4 - len(remaining_services))]
    
    # Randomly select add-on services with a minimum of 4
    add_on_service_count = random.randint(4, len(remaining_services))
    add_on_services = [
        AddOnService(name=service) for service in random.sample(remaining_services, add_on_service_count)
    ]
    
    # Randomly select tags with a minimum of 3
    tag_count = random.randint(3, len(subcategory.tags))
    tags = random.sample(subcategory.tags, tag_count)
    
    # Randomize unavailability_list
    unavailability_list = [
        datetime.now() + timedelta(days=random.randint(1, 30)) for _ in range(random.randint(2, 5))
    ]
    
    # Randomize working_hours
    start_hour = random.randint(6, 10)  # Random start time between 6 AM and 10 AM
    end_hour = start_hour + random.randint(6, 10)  # Random end time between 6 to 10 hours after start
    working_hours = (
        time(hour=start_hour, minute=random.randint(0, 59)),
        time(hour=end_hour % 24, minute=random.randint(0, 59))
    )
    
    # Generate a unique worker profile
    return WorkerProfile(
        uid=f"worker_{uid}",
        field_of_work=subcategory.name,
        description=category.name,
        location=location,
        quick_fixes=[f"QuickFix_{random.randint(1, 100)}" for _ in range(3)],
        included_services=included_services,
        add_on_services=add_on_services,
        reviews=deque([
            Review(
                username=f"User{random.randint(1, 100)}",
                review=random_feedback(),
                rating=random.uniform(3, 5)
            )
            for _ in range(5)
        ]),
        profile_picture=random_picture(),
        banner_picture=random_picture(),
        price=random.uniform(50, 200),
        display_name=f"Worker {uid}",
        unavailability_list=unavailability_list,
        working_hours=working_hours,
        tags=tags,
    )

def main():
    # Initialize Firestore
    db = initialize_firestore()

    # Categories data
    from upload_categories import categories

    # Generate 20 random workers
    workers = [generate_random_worker_profile(uid, categories) for uid in range(1, 151)]

    # Upload workers to Firestore
    upload_data(db, workers)

if __name__ == "__main__":
    main()