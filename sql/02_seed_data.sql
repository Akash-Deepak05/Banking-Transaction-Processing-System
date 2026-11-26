-- ============================================================
-- Banking Transaction System - Seed Data
-- ============================================================

-- ============================================================
-- CUSTOMERS
-- ============================================================
INSERT INTO customer (customer_id, full_name, email, phone, address) VALUES
('CUST001', 'Ravi Kumar',      'ravi.kumar@email.com',      '9876543210', '12 MG Road, Bangalore'),
('CUST002', 'Priya Sharma',    'priya.sharma@email.com',    '9876543211', '45 Park Street, Mumbai'),
('CUST003', 'Amit Singh',      'amit.singh@email.com',      '9876543212', '78 Nehru Nagar, Delhi'),
('CUST004', 'Sunita Patel',    'sunita.patel@email.com',    '9876543213', '23 Lake View, Pune'),
('CUST005', 'Rahul Verma',     'rahul.verma@email.com',     '9876543214', '56 Civil Lines, Jaipur'),
('CUST006', 'Deepa Nair',      'deepa.nair@email.com',      '9876543215', '89 Anna Salai, Chennai'),
('CUST007', 'Vikram Mehta',    'vikram.mehta@email.com',    '9876543216', '34 Banjara Hills, Hyderabad'),
('CUST008', 'Anjali Gupta',    'anjali.gupta@email.com',    '9876543217', '67 Salt Lake, Kolkata'),
('CUST009', 'Suresh Reddy',    'suresh.reddy@email.com',    '9876543218', '90 Koramangala, Bangalore'),
('CUST010', 'Meena Joshi',     'meena.joshi@email.com',     '9876543219', '12 Sector 15, Noida'),
('CUST011', 'Arun Pillai',     'arun.pillai@email.com',     '9876543220', '45 MG Road, Kochi'),
('CUST012', 'Kavita Desai',    'kavita.desai@email.com',    '9876543221', '78 Juhu, Mumbai'),
('CUST013', 'Nitin Jain',      'nitin.jain@email.com',      '9876543222', '23 Lajpat Nagar, Delhi'),
('CUST014', 'Pooja Yadav',     'pooja.yadav@email.com',     '9876543223', '56 Gomti Nagar, Lucknow'),
('CUST015', 'Sanjay Chandra',  'sanjay.chandra@email.com',  '9876543224', '89 Alkapuri, Vadodara');

-- ============================================================
-- ACCOUNTS
-- ============================================================
INSERT INTO account (account_id, customer_id, account_type, balance, status) VALUES
('ACC1001', 'CUST001', 'SAVINGS',       100000.00, 'ACTIVE'),
('ACC1002', 'CUST001', 'CURRENT',        50000.00, 'ACTIVE'),
('ACC2001', 'CUST002', 'SAVINGS',        75000.00, 'ACTIVE'),
('ACC2002', 'CUST002', 'CURRENT',        25000.00, 'ACTIVE'),
('ACC3001', 'CUST003', 'SAVINGS',       200000.00, 'ACTIVE'),
('ACC4001', 'CUST004', 'SAVINGS',        30000.00, 'ACTIVE'),
('ACC5001', 'CUST005', 'SAVINGS',       150000.00, 'ACTIVE'),
('ACC6001', 'CUST006', 'SAVINGS',        80000.00, 'ACTIVE'),
('ACC7001', 'CUST007', 'CURRENT',       500000.00, 'ACTIVE'),
('ACC8001', 'CUST008', 'SAVINGS',        45000.00, 'ACTIVE'),
('ACC9001', 'CUST009', 'SAVINGS',        60000.00, 'ACTIVE'),
('ACC10001','CUST010', 'SAVINGS',        90000.00, 'ACTIVE'),
('ACC11001','CUST011', 'SAVINGS',        35000.00, 'ACTIVE'),
('ACC12001','CUST012', 'CURRENT',       120000.00, 'ACTIVE'),
('ACC13001','CUST013', 'SAVINGS',        55000.00, 'ACTIVE'),
('ACC14001','CUST014', 'SAVINGS',        40000.00, 'ACTIVE'),
('ACC15001','CUST015', 'CURRENT',       250000.00, 'ACTIVE'),
-- Inactive account for testing rejection
('ACC_INACT','CUST001','SAVINGS',        10000.00, 'INACTIVE'),
-- Low balance account for testing insufficient funds
('ACC_LOW',  'CUST002','SAVINGS',          500.00, 'ACTIVE'),
-- Concurrency test account (₹10,000 for the mandatory test)
('ACC_CONC', 'CUST003','SAVINGS',        10000.00, 'ACTIVE');
