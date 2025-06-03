-- Create User Profiles table (extends Supabase auth.users)
CREATE TABLE user_profiles (
    user_id UUID REFERENCES auth.users(id) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('admin', 'technician', 'manager')),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create Equipment table
CREATE TABLE equipment (
    equipment_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    identifier VARCHAR(100) UNIQUE NOT NULL,
    type VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    location VARCHAR(255),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive', 'unavailable'))
);

-- Create Breakdowns table
CREATE TABLE breakdowns (
    breakdown_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    reporter_id UUID REFERENCES auth.users(id),
    equipment_id UUID REFERENCES equipment(equipment_id),
    urgency_level VARCHAR(20) NOT NULL CHECK (urgency_level IN ('low', 'medium', 'high', 'critical')),
    location VARCHAR(255),
    description TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'open' CHECK (status IN ('open', 'in_progress', 'completed', 'cancelled')),
    reported_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    estimated_completion TIMESTAMP WITH TIME ZONE
);

-- Create Assignments table
CREATE TABLE assignments (
    assignment_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    breakdown_id UUID REFERENCES breakdowns(breakdown_id),
    technician_id UUID REFERENCES auth.users(id),
    assigned_by UUID REFERENCES auth.users(id),
    assigned_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'inactive')),
    reassigned BOOLEAN DEFAULT FALSE
);

-- Create BreakdownPhotos table
CREATE TABLE breakdown_photos (
    photo_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    breakdown_id UUID REFERENCES breakdowns(breakdown_id),
    photo_url TEXT NOT NULL,
    uploaded_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create BreakdownStatusHistory table
CREATE TABLE breakdown_status_history (
    history_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    breakdown_id UUID REFERENCES breakdowns(breakdown_id),
    status VARCHAR(20) NOT NULL,
    changed_by UUID REFERENCES auth.users(id),
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create TechnicianMetrics table
CREATE TABLE technician_metrics (
    technician_id UUID REFERENCES auth.users(id) PRIMARY KEY,
    total_breakdowns_handled INTEGER DEFAULT 0,
    average_resolution_time INTERVAL
);

-- Create Messages table
CREATE TABLE messages (
    message_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    sender_id UUID REFERENCES auth.users(id),
    receiver_id UUID REFERENCES auth.users(id),
    breakdown_id UUID REFERENCES breakdowns(breakdown_id),
    content TEXT NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Create indexes for better performance
CREATE INDEX idx_breakdowns_status ON breakdowns(status);
CREATE INDEX idx_breakdowns_urgency ON breakdowns(urgency_level);
CREATE INDEX idx_assignments_technician ON assignments(technician_id);
CREATE INDEX idx_assignments_breakdown ON assignments(breakdown_id);
CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_messages_receiver ON messages(receiver_id);

-- Enable Row Level Security (RLS)
ALTER TABLE user_profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE equipment ENABLE ROW LEVEL SECURITY;
ALTER TABLE breakdowns ENABLE ROW LEVEL SECURITY;
ALTER TABLE assignments ENABLE ROW LEVEL SECURITY;
ALTER TABLE breakdown_photos ENABLE ROW LEVEL SECURITY;
ALTER TABLE breakdown_status_history ENABLE ROW LEVEL SECURITY;
ALTER TABLE technician_metrics ENABLE ROW LEVEL SECURITY;
ALTER TABLE messages ENABLE ROW LEVEL SECURITY;

-- Basic RLS policies
CREATE POLICY "Users can read own profile" ON user_profiles
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can update own profile" ON user_profiles
    FOR UPDATE USING (auth.uid() = user_id);

-- All authenticated users can read equipment
CREATE POLICY "Authenticated users can read equipment" ON equipment
    FOR SELECT TO authenticated USING (true);

-- All authenticated users can read breakdowns
CREATE POLICY "Authenticated users can read breakdowns" ON breakdowns
    FOR SELECT TO authenticated USING (true);

-- Users can create breakdowns
CREATE POLICY "Authenticated users can create breakdowns" ON breakdowns
    FOR INSERT TO authenticated WITH CHECK (true);

-- Insert sample equipment data
INSERT INTO equipment (identifier, type, model, location) VALUES
('EQ001', 'Generator', 'CAT-500', 'Building A'),
('EQ002', 'HVAC Unit', 'Carrier-200', 'Building B'),
('EQ003', 'Elevator', 'Otis-X1', 'Building C');

CREATE POLICY "Admins can read all profiles" ON user_profiles
    FOR SELECT TO authenticated USING (
        EXISTS (
            SELECT 1 FROM user_profiles
            WHERE user_id = auth.uid()
            AND role = 'admin'
            AND status = 'active'
        )
    );

CREATE POLICY "Admins can update all profiles" ON user_profiles
    FOR UPDATE TO authenticated USING (
        EXISTS (
            SELECT 1 FROM user_profiles
            WHERE user_id = auth.uid()
            AND role = 'admin'
            AND status = 'active'
        )
    );

CREATE POLICY "Admins can insert profiles" ON user_profiles
    FOR INSERT TO authenticated WITH CHECK (
        EXISTS (
            SELECT 1 FROM user_profiles
            WHERE user_id = auth.uid()
            AND role = 'admin'
            AND status = 'active'
        )
    );

CREATE POLICY "Admins can delete profiles" ON user_profiles
    FOR DELETE TO authenticated USING (
        EXISTS (
            SELECT 1 FROM user_profiles
            WHERE user_id = auth.uid()
            AND role = 'admin'
            AND status = 'active'
        )
    );

-- Function to update user email (requires admin privileges)
CREATE OR REPLACE FUNCTION admin_update_user_email(
    target_user_id UUID,
    new_email TEXT
)
RETURNS JSON
LANGUAGE plpgsql
SECURITY DEFINER -- This allows the function to bypass RLS and access auth schema
SET search_path = public, auth
AS $$
DECLARE
    requesting_user_id UUID;
    result JSON;
BEGIN
    -- Get the current user ID
    requesting_user_id := auth.uid();

    -- Check if the requesting user is an admin
    IF NOT EXISTS (
        SELECT 1 FROM public.user_profiles
        WHERE user_id = requesting_user_id
        AND role = 'admin'
        AND status = 'active'
    ) THEN
        RAISE EXCEPTION 'Unauthorized: Only active admins can update user emails'
            USING ERRCODE = 'insufficient_privilege';
    END IF;

    -- Check if target user exists
    IF NOT EXISTS (SELECT 1 FROM auth.users WHERE id = target_user_id) THEN
        RAISE EXCEPTION 'Target user not found'
            USING ERRCODE = 'invalid_parameter_value';
    END IF;

    -- Update the user's email in auth.users table
    UPDATE auth.users
    SET
        email = new_email,
        email_confirmed_at = NOW(),
        raw_user_meta_data = COALESCE(raw_user_meta_data, '{}'::jsonb) || jsonb_build_object('email', new_email),
        updated_at = NOW()
    WHERE id = target_user_id;

    -- Return success result
    SELECT json_build_object(
        'success', true,
        'message', 'Email updated successfully',
        'user_id', target_user_id,
        'new_email', new_email
    ) INTO result;

    RETURN result;

EXCEPTION
    WHEN OTHERS THEN
        -- Return error result
        SELECT json_build_object(
            'success', false,
            'error', SQLERRM,
            'error_code', SQLSTATE
        ) INTO result;

        RETURN result;
END;
$$;

-- Function to get user by email (for admin purposes)
CREATE OR REPLACE FUNCTION admin_get_user_by_email(user_email TEXT)
RETURNS JSON
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, auth
AS $$
DECLARE
    requesting_user_id UUID;
    user_data JSON;
BEGIN
    requesting_user_id := auth.uid();

    -- Check if the requesting user is an admin
    IF NOT EXISTS (
        SELECT 1 FROM public.user_profiles
        WHERE user_id = requesting_user_id
        AND role = 'admin'
        AND status = 'active'
    ) THEN
        RAISE EXCEPTION 'Unauthorized: Only active admins can access user data'
            USING ERRCODE = 'insufficient_privilege';
    END IF;

    -- Get user data
    SELECT json_build_object(
        'id', u.id,
        'email', u.email,
        'created_at', u.created_at,
        'email_confirmed_at', u.email_confirmed_at,
        'profile', json_build_object(
            'name', p.name,
            'role', p.role,
            'status', p.status,
            'created_at', p.created_at
        )
    )
    INTO user_data
    FROM auth.users u
    LEFT JOIN public.user_profiles p ON u.id = p.user_id
    WHERE u.email = user_email;

    RETURN COALESCE(user_data, json_build_object('error', 'User not found'));
END;
$$;

-- Grant execute permissions to authenticated users
GRANT EXECUTE ON FUNCTION admin_update_user_email TO authenticated;
GRANT EXECUTE ON FUNCTION admin_get_user_by_email TO authenticated;

-- Create a view for admins to see all users (optional)
CREATE OR REPLACE VIEW admin_users_view AS
SELECT
    u.id as user_id,
    u.email,
    u.created_at as user_created_at,
    u.email_confirmed_at,
    p.name,
    p.role,
    p.status,
    p.created_at as profile_created_at
FROM auth.users u
LEFT JOIN public.user_profiles p ON u.id = p.user_id
ORDER BY p.created_at DESC;

-- Create RLS policy for the view (only admins can access)
CREATE POLICY "Only admins can access admin_users_view" ON admin_users_view
    FOR SELECT TO authenticated USING (
        EXISTS (
            SELECT 1 FROM user_profiles
            WHERE user_id = auth.uid()
            AND role = 'admin'
            AND status = 'active'
        )
    );
