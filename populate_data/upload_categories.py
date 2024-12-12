import firebase_admin
from firebase_admin import credentials, firestore
from dataclasses import dataclass
from typing import List

@dataclass
class Scale:
    longScale: str
    shortScale: str

@dataclass
class Subcategory:
    id: str
    name: str
    category: str
    tags: List[str]
    scale: Scale
    setService: List[str]

@dataclass
class Category:
    id: str
    name: str
    description: str
    subcategories: List[Subcategory]

def initialize_firestore():
    # Initialize the Firebase app
    cred = credentials.Certificate('../data_loader_service_account_key.json')
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
                'category': subcategory.category,
                'tags': subcategory.tags,
                'scale': {
                      'longScale': subcategory.scale.longScale,
                      'shortScale': subcategory.scale.shortScale
                },
                'setService': subcategory.setService
            }
            subcategory_ref.set(subcategory_data)

    print("Data uploaded successfully.")

categories = [
    Category(
        id="painting",
        name="Painting",
        description="Find skilled painters for residential or commercial projects.",
        subcategories=[
            Subcategory(
                id="residential_painting",
                name="Residential Painting",
                category="Painting",
                tags=[
                    "Interior Painting",
                    "Exterior Painting",
                    "Cabinet Painting",
                    "Ceiling Painting",
                    "Varnishing",
                    "Accent Walls"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of painting a 20 m² room.",
                    shortScale="20 m² room equivalent"
                ),
                setService=[
                    "Surface Preparation",
                    "Interior Painting",
                    "Exterior Painting",
                    "Cabinet Painting",
                    "Trim and Baseboard Painting",
                    "Wallpaper Removal",
                    "Deck and Fence Painting",
                    "Popcorn Ceiling Removal",
                    "Pressure Washing",
                    "Garage Floor Painting",
                    "Sealing and Caulking",
                    "Color Consultation",
                    "Minor Repairs",
                    "Clean-Up"
                ]
            ),
            Subcategory(
                id="commercial_painting",
                name="Commercial Painting",
                category="Painting",
                tags=[
                    "Office Buildings",
                    "Retail Spaces",
                    "Warehouse Painting",
                    "Industrial Facilities",
                    "High-Rise Buildings",
                    "Educational Institutions"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of painting a 100 m² commercial space.",
                    shortScale="100 m² commercial space equivalent"
                ),
                setService=[
                    "Surface Preparation",
                    "Interior Commercial Painting",
                    "Exterior Commercial Painting",
                    "Specialty Coatings",
                    "Epoxy Floor Coatings",
                    "Line Striping and Markings",
                    "Power Washing",
                    "Graffiti Removal",
                    "Metal Structure Painting",
                    "Parking Lot Painting",
                    "Safety Painting",
                    "Color Branding",
                    "Clean-Up"
                ]
            ),
            Subcategory(
                id="decorative_painting",
                name="Decorative Painting",
                category="Painting",
                tags=[
                    "Faux Finishes",
                    "Murals",
                    "Trompe l'oeil",
                    "Gold Leafing",
                    "Theme Rooms",
                    "Artistic Design"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of painting a 20 m² room.",
                    shortScale="20 m² room equivalent"
                ),
                setService=[
                    "Decorative Painting",
                    "Faux Finishes",
                    "Murals",
                    "Accent Walls",
                    "Textured Painting",
                    "Stenciling",
                    "Color Washing",
                    "Rag Rolling",
                    "Sponging",
                    "Venetian Plaster",
                    "Glazing",
                    "Metallic Finishes",
                    "Surface Preparation",
                    "Color Consultation",
                    "Clean-Up"
                ]
            ),
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
                category="Plumbing",
                tags=[
                    "Leak Repair",
                    "Pipe Installation",
                    "Water Heater Repair",
                    "Bathroom Plumbing",
                    "Kitchen Plumbing",
                    "Plumbing Inspections"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of fixing a standard household leak.",
                    shortScale="Standard leak repair equivalent"
                ),
                setService=[
                    "Leak Detection and Repair",
                    "Pipe Installation and Replacement",
                    "Water Heater Installation and Repair",
                    "Drain Cleaning",
                    "Toilet Repair and Installation",
                    "Faucet and Fixture Installation",
                    "Garbage Disposal Repair",
                    "Sewer Line Repair",
                    "Sump Pump Installation",
                    "Bathroom Remodeling",
                    "Emergency Plumbing Services",
                    "Water Filtration Systems",
                    "Backflow Prevention",
                    "Septic Tank Maintenance",
                    "Clean-Up"
                ]
            ),
            Subcategory(
                id="commercial_plumbing",
                name="Commercial Plumbing",
                category="Plumbing",
                tags=[
                    "Sewer Systems",
                    "Gas Line Installation",
                    "Industrial Plumbing",
                    "Medical Facilities",
                    "Code Compliance",
                    "24/7 Services"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of servicing a commercial plumbing system.",
                    shortScale="Commercial plumbing service equivalent"
                ),
                setService=[
                    "Commercial Pipe Installation",
                    "Sewer System Maintenance",
                    "Gas Line Installation and Repair",
                    "Hydro Jetting",
                    "Grease Trap Installation",
                    "Boiler System Installation",
                    "Sprinkler System Installation",
                    "Backflow Services",
                    "Water Main Installation",
                    "Commercial Water Heater Services",
                    "Emergency Plumbing Services",
                    "Fixture Installation",
                    "Plumbing System Design",
                    "Preventive Maintenance",
                    "Clean-Up"
                ]
            ),
        ]
    ),
    Category(
        id="gardening",
        name="Gardening",
        description="Hire professional gardeners for landscaping and maintenance.",
        subcategories=[
            Subcategory(
                id="landscaping",
                name="Landscaping",
                category="Gardening",
                tags=[
                    "Garden Design",
                    "Lawn Installation",
                    "Landscape Architecture",
                    "Sustainable Landscaping",
                    "Xeriscaping",
                    "Erosion Control"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of designing a small garden area.",
                    shortScale="Small garden design equivalent"
                ),
                setService=[
                    "Garden Design",
                    "Lawn Installation",
                    "Hardscaping",
                    "Irrigation System Installation",
                    "Planting and Transplanting",
                    "Soil Preparation",
                    "Landscape Lighting",
                    "Water Features Installation",
                    "Patio and Deck Construction",
                    "Retaining Walls",
                    "Mulching",
                    "Seasonal Planting",
                    "Tree and Shrub Planting",
                    "Pathways and Walkways",
                    "Clean-Up"
                ]
            ),
            Subcategory(
                id="maintenance",
                name="Maintenance",
                category="Gardening",
                tags=[
                    "Weed Control",
                    "Hedge Trimming",
                    "Lawn Care",
                    "Aeration",
                    "Organic Gardening",
                    "Plant Health Care"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of monthly garden maintenance.",
                    shortScale="Monthly maintenance equivalent"
                ),
                setService=[
                    "Lawn Mowing",
                    "Weed Control",
                    "Hedge Trimming",
                    "Pruning and Deadheading",
                    "Fertilization",
                    "Pest and Disease Control",
                    "Leaf Removal",
                    "Garden Clean-Up",
                    "Seasonal Maintenance",
                    "Irrigation System Maintenance",
                    "Tree Trimming",
                    "Soil Testing",
                    "Composting",
                    "Garden Waste Removal",
                    "Mulching",
                    "Clean-Up"
                ]
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
                category="Electrical Work",
                tags=[
                    "Wiring",
                    "Lighting Installation",
                    "Smart Home Integration",
                    "Energy Efficiency",
                    "Electrical Safety",
                    "Emergency Services"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of installing standard home wiring.",
                    shortScale="Standard wiring installation equivalent"
                ),
                setService=[
                    "Wiring and Rewiring",
                    "Lighting Installation",
                    "Ceiling Fan Installation",
                    "Electrical Panel Upgrades",
                    "Outlet and Switch Installation",
                    "Home Automation Systems",
                    "Smoke Detector Installation",
                    "Surge Protection",
                    "Electrical Inspections",
                    "Landscape Lighting",
                    "Backup Generator Installation",
                    "Troubleshooting and Repairs",
                    "Electric Vehicle Charger Installation",
                    "Security System Installation",
                    "Clean-Up"
                ]
            ),
            Subcategory(
                id="commercial_electrical",
                name="Commercial Electrical Services",
                category="Electrical Work",
                tags=[
                    "Industrial Equipment",
                    "Security Systems",
                    "High Voltage Systems",
                    "UPS Systems",
                    "Fire Alarm Systems",
                    "Access Control Systems"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of installing commercial electrical systems.",
                    shortScale="Commercial electrical service equivalent"
                ),
                setService=[
                    "Industrial Equipment Wiring",
                    "Security and Alarm Systems",
                    "Data and Communication Lines",
                    "Energy Efficient Lighting",
                    "Electrical Maintenance",
                    "Emergency Lighting Systems",
                    "Transformer Installation",
                    "Electrical System Design",
                    "HVAC Wiring",
                    "Backup Generator Systems",
                    "Compliance Upgrades",
                    "Motor Control Systems",
                    "Building Automation",
                    "Panel Installation and Upgrades",
                    "Clean-Up"
                ]
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
                category="Handyman Services",
                tags=[
                    "Furniture Assembly",
                    "Fixture Replacement",
                    "Home Repairs",
                    "Installation Services",
                    "Maintenance Tasks",
                    "Home Improvements"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of a standard home repair task.",
                    shortScale="Standard repair equivalent"
                ),
                setService=[
                    "Furniture Assembly",
                    "Fixture Replacement",
                    "Drywall Repair",
                    "Door and Window Repair",
                    "Shelving Installation",
                    "Minor Carpentry",
                    "Picture Hanging",
                    "Caulking",
                    "Tile Repair",
                    "Minor Plumbing Repairs",
                    "Gutter Repair",
                    "Fence Repair",
                    "Lock Replacement",
                    "Hardware Installation",
                    "Clean-Up"
                ]
            ),
            Subcategory(
                id="home_maintenance",
                name="Home Maintenance",
                category="Handyman Services",
                tags=[
                    "Gutter Cleaning",
                    "Pressure Washing",
                    "Seasonal Cleanup",
                    "Pest Control",
                    "Home Safety Checks",
                    "Roof Maintenance"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of a standard maintenance task.",
                    shortScale="Standard maintenance equivalent"
                ),
                setService=[
                    "Gutter Cleaning",
                    "Pressure Washing",
                    "Deck Staining",
                    "Grout Cleaning",
                    "Air Filter Replacement",
                    "Smoke Detector Testing",
                    "Seasonal Maintenance",
                    "Window Cleaning",
                    "Exterior Maintenance",
                    "Yard Work Assistance",
                    "Weatherproofing",
                    "Insulation Installation",
                    "Appliance Maintenance",
                    "Garage Organization",
                    "Clean-Up"
                ]
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
                category="Cleaning Services",
                tags=[
                    "Regular Cleaning",
                    "Deep Cleaning",
                    "Spring Cleaning",
                    "Allergen Reduction",
                    "Pet-friendly Cleaning",
                    "One-Time Cleaning"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of cleaning a standard 3-bedroom house.",
                    shortScale="Standard house cleaning equivalent"
                ),
                setService=[
                    "Regular Cleaning",
                    "Deep Cleaning",
                    "Move-In/Move-Out Cleaning",
                    "Post-Construction Cleaning",
                    "Appliance Cleaning",
                    "Carpet Cleaning",
                    "Window Cleaning",
                    "Upholstery Cleaning",
                    "Floor Polishing",
                    "Bathroom Sanitization",
                    "Kitchen Cleaning",
                    "Organization Services",
                    "Green Cleaning",
                    "Laundry Services",
                    "Clean-Up"
                ]
            ),
            Subcategory(
                id="commercial_cleaning",
                name="Commercial Cleaning",
                category="Cleaning Services",
                tags=[
                    "Office Cleaning",
                    "Window Cleaning",
                    "Day Porter Services",
                    "Retail Spaces",
                    "Sanitization Services",
                    "Building Maintenance"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of cleaning a standard office space.",
                    shortScale="Standard office cleaning equivalent"
                ),
                setService=[
                    "Office Cleaning",
                    "Janitorial Services",
                    "Window Cleaning",
                    "Carpet and Floor Maintenance",
                    "Restroom Sanitization",
                    "Trash Removal",
                    "Surface Disinfection",
                    "Event Cleanup",
                    "High-Rise Window Cleaning",
                    "Industrial Cleaning",
                    "Medical Facility Cleaning",
                    "Restaurant Cleaning",
                    "Warehouse Cleaning",
                    "Eco-Friendly Cleaning",
                    "Clean-Up"
                ]
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
                category="Carpentry",
                tags=[
                    "Custom Furniture",
                    "Restoration",
                    "Handcrafted Woodwork",
                    "Modern Designs",
                    "Wooden Art Pieces",
                    "Bespoke Joinery"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of crafting a custom piece of furniture.",
                    shortScale="Custom furniture equivalent"
                ),
                setService=[
                    "Custom Furniture Design",
                    "Furniture Restoration",
                    "Cabinet Making",
                    "Shelving Units",
                    "Built-In Closets",
                    "Table and Chair Construction",
                    "Antique Repair",
                    "Wood Finishing and Staining",
                    "Upholstery Services",
                    "Furniture Assembly",
                    "Outdoor Furniture",
                    "Furniture Modification",
                    "Wood Carving",
                    "Veneer Work",
                    "Clean-Up"
                ]
            ),
            Subcategory(
                id="construction_carpentry",
                name="Construction Carpentry",
                category="Carpentry",
                tags=[
                    "Framing",
                    "Deck Building",
                    "Structural Repairs",
                    "Custom Woodwork",
                    "Building Codes",
                    "Project Management"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of framing a standard room.",
                    shortScale="Standard room framing equivalent"
                ),
                setService=[
                    "Framing",
                    "Deck Building",
                    "Door and Window Installation",
                    "Siding Installation",
                    "Roofing Support",
                    "Floor Installation",
                    "Staircase Construction",
                    "Trim and Molding Installation",
                    "Gazebo and Pergola Construction",
                    "Drywall Installation",
                    "Basement Finishing",
                    "Renovations and Additions",
                    "Demolition and Removal",
                    "Insulation Installation",
                    "Clean-Up"
                ]
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
                category="Moving Services",
                tags=[
                    "Home Moves",
                    "Office Moves",
                    "Same-Day Service",
                    "Senior Moving",
                    "Affordable Rates",
                    "Professional Movers"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of moving a standard 2-bedroom apartment locally.",
                    shortScale="Standard local move equivalent"
                ),
                setService=[
                    "Home Moving",
                    "Office Moving",
                    "Packing and Unpacking",
                    "Furniture Disassembly and Reassembly",
                    "Loading and Unloading",
                    "Specialty Item Moving (e.g., pianos, artwork)",
                    "Temporary Storage Solutions",
                    "Packing Materials Supply",
                    "Moving Insurance",
                    "In-Home Moves (rearranging furniture)",
                    "Appliance Moving",
                    "Vehicle Transportation",
                    "Cleaning Services Post-Move",
                    "Eco-Friendly Moving Options",
                    "Clean-Up"
                ]
            ),
            Subcategory(
                id="long_distance_moving",
                name="Long Distance Moving",
                category="Moving Services",
                tags=[
                    "Interstate Moves",
                    "International Moves",
                    "Cross-Country Moving",
                    "Corporate Relocation",
                    "Military Moves",
                    "Full-Service Moving"
                ],
                scale=Scale(
                    longScale="Prices are displayed relative to the cost of moving a standard household across states.",
                    shortScale="Standard long-distance move equivalent"
                ),
                setService=[
                    "Interstate Moving",
                    "International Moving",
                    "Customs Clearance Assistance",
                    "Secure Packaging for Long Hauls",
                    "Vehicle Shipping",
                    "Storage-in-Transit Services",
                    "Shipment Tracking",
                    "Pet Relocation Services",
                    "Insurance and Valuation Coverage",
                    "Special Handling for Fragile Items",
                    "Unpacking and Setup",
                    "Documentation Assistance",
                    "Air and Sea Freight Options",
                    "Consultation and Planning",
                    "Clean-Up"
                ]
            ),
        ]
    ),
]

def main():
    db = initialize_firestore()

    # Upload the data to Firestore
    upload_data(db, categories)

if __name__ == '__main__':
    main()