import firebase_admin
from firebase_admin import credentials, firestore
from dataclasses import dataclass
from typing import List

@dataclass
class Subcategory:
    id: str
    name: str
    tags: List[str]

@dataclass
class Category:
    id: str
    name: str
    description: str
    subcategories: List[Subcategory]

def initialize_firestore():
    # Initialize the Firebase app
    cred = credentials.Certificate('data_loader_service_account_key.json')
    firebase_admin.initialize_app(cred)
    return firestore.client()

def upload_data(db, categories: List[Category]):
    for category in categories:
        # Create or update the category document
        category_ref = db.collection('categories').document(category.id)
        category_data = {
            'name': category.name,
            'description': category.description
        }
        category_ref.set(category_data)

        # Upload subcategories
        for subcategory in category.subcategories:
            subcategory_ref = category_ref.collection('subcategories').document(subcategory.id)
            subcategory_data = {
                'name': subcategory.name,
                'tags': subcategory.tags
            }
            subcategory_ref.set(subcategory_data)

    print("Data uploaded successfully.")

def main():
    db = initialize_firestore()

    # Define your categories, subcategories, and tags
    categories = [
        Category(
            id="painting",
            name="Painting",
            description="Find skilled painters for residential or commercial projects.",
            subcategories=[
                Subcategory(
                    id="residential_painting",
                    name="Residential Painting",
                    tags=["Interior Painting", "Exterior Painting", "Cabinet Painting"]
                ),
                Subcategory(
                    id="commercial_painting",
                    name="Commercial Painting",
                    tags=["Office Buildings", "Retail Spaces"]
                ),
                Subcategory(
                    id="decorative_painting",
                    name="Decorative Painting",
                    tags=["Faux Finishes", "Murals"]
                ),
                # Add more subcategories as needed
            ]
        ),
        Category(
            id="plumbing",
            name="Plumbing",
            description="Connect with expert plumbers for repairs and installations.",
            subcategories=[
                Subcategory(
                    id="residential_plumbing",
                    name="Residential Plumbing",
                    tags=["Leak Repair", "Pipe Installation", "Water Heater Repair"]
                ),
                Subcategory(
                    id="commercial_plumbing",
                    name="Commercial Plumbing",
                    tags=["Sewer Systems", "Gas Line Installation"]
                ),
                # Add more subcategories as needed
            ]
        ),
        # Add other categories similarly
        Category(
            id="gardening",
            name="Gardening",
            description="Hire professional gardeners for landscaping and maintenance.",
            subcategories=[
                Subcategory(
                    id="landscaping",
                    name="Landscaping",
                    tags=["Garden Design", "Lawn Installation"]
                ),
                Subcategory(
                    id="maintenance",
                    name="Maintenance",
                    tags=["Weed Control", "Hedge Trimming"]
                ),
            ]
        ),
        Category(
            id="electrical_work",
            name="Electrical Work",
            description="Locate certified electricians for safe and efficient service.",
            subcategories=[
                Subcategory(
                    id="residential_electrical",
                    name="Residential Electrical Services",
                    tags=["Wiring", "Lighting Installation"]
                ),
                Subcategory(
                    id="commercial_electrical",
                    name="Commercial Electrical Services",
                    tags=["Industrial Equipment", "Security Systems"]
                ),
            ]
        ),
        Category(
            id="handyman",
            name="Handyman Services",
            description="Get help with various minor home repairs and tasks.",
            subcategories=[
                Subcategory(
                    id="general_repairs",
                    name="General Repairs",
                    tags=["Furniture Assembly", "Fixture Replacement"]
                ),
                Subcategory(
                    id="home_maintenance",
                    name="Home Maintenance",
                    tags=["Gutter Cleaning", "Pressure Washing"]
                ),
            ]
        ),
        Category(
            id="cleaning",
            name="Cleaning Services",
            description="Book reliable cleaners for home or office maintenance.",
            subcategories=[
                Subcategory(
                    id="residential_cleaning",
                    name="Residential Cleaning",
                    tags=["Regular Cleaning", "Deep Cleaning"]
                ),
                Subcategory(
                    id="commercial_cleaning",
                    name="Commercial Cleaning",
                    tags=["Office Cleaning", "Window Cleaning"]
                ),
            ]
        ),
        Category(
            id="carpentry",
            name="Carpentry",
            description="Hire experienced carpenters for woodwork and construction tasks.",
            subcategories=[
                Subcategory(
                    id="furniture_carpentry",
                    name="Furniture Carpentry",
                    tags=["Custom Furniture", "Restoration"]
                ),
                Subcategory(
                    id="construction_carpentry",
                    name="Construction Carpentry",
                    tags=["Framing", "Deck Building"]
                ),
            ]
        ),
        Category(
            id="moving",
            name="Moving Services",
            description="Find professional movers to help with local or long-distance relocation tasks.",
            subcategories=[
                Subcategory(
                    id="local_moving",
                    name="Local Moving",
                    tags=["Home Moves", "Office Moves"]
                ),
                Subcategory(
                    id="long_distance_moving",
                    name="Long Distance Moving",
                    tags=["Interstate Moves", "International Moves"]
                ),
            ]
        ),
    ]

    # Upload the data to Firestore
    upload_data(db, categories)

if __name__ == '__main__':
    main()